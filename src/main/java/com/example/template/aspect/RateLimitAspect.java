package com.example.template.aspect;

import com.example.template.common.annotation.RateLimit;
import com.example.template.common.constant.Constants;
import com.example.template.common.exception.BusinessException;
import com.example.template.common.result.ResultCode;
import com.example.template.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 接口限流切面
 * <p>
 * 拦截标注了 @RateLimit 注解的 Controller 方法，通过 Redis + Lua 脚本实现分布式限流。
 * <p>
 * 原理：
 * 1. 为每个接口 + 用户生成 Redis Key
 * 2. 在时间窗口内累计请求次数
 * 3. 超过阈值则拒绝请求（抛出 BusinessException）
 * <p>
 * 使用 Lua 脚本保证"读取-判断-递增-设置过期"的原子性，
 * 避免并发下多个请求同时通过限流检查。
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Lua 限流脚本
     * 逻辑：
     * 1. 读取当前计数，如果已达到阈值，返回 0（拒绝）
     * 2. 递增计数，如果是第一次，设置过期时间
     * 3. 返回 1（允许）
     * <p>
     * 使用 Lua 保证上述操作的原子性
     */
    private static final String LUA_SCRIPT = """
        local key = KEYS[1]           -- 限流 Key
        local count = tonumber(ARGV[1])  -- 最大允许次数
        local time = tonumber(ARGV[2])   -- 时间窗口（秒）
        local current = tonumber(redis.call('get', key) or '0')  -- 当前已请求次数
        if current >= count then
            return 0                -- 已达阈值，拒绝
        end
        current = redis.call('incr', key)  -- 递增计数
        if current == 1 then
            redis.call('expire', key, time)  -- 首次访问设置过期时间
        end
        return 1                    -- 允许访问
        """;

    /** Redis 脚本封装，指定返回类型为 Long */
    private final DefaultRedisScript<Long> script = new DefaultRedisScript<>(LUA_SCRIPT, Long.class);

    /**
     * 环绕通知：在方法执行前检查限流
     */
    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint point, RateLimit rateLimit) throws Throwable {
        // 构建限流 Key
        String key = buildKey(point, rateLimit);

        // 执行 Lua 脚本进行原子性限流判断
        Long result = redisTemplate.execute(
                script,
                Collections.singletonList(key),
                String.valueOf(rateLimit.count()),
                String.valueOf(rateLimit.time())
        );

        // 返回 0 表示已被限流
        if (result != null && result == 0) {
            log.warn("[限流] key={} 超过限制 {}/{}s", key, rateLimit.count(), rateLimit.time());
            throw new BusinessException(ResultCode.RATE_LIMIT.getCode(), rateLimit.message());
        }

        // 放行，执行目标方法
        return point.proceed();
    }

    /**
     * 构建限流 Key
     * 格式：rate_limit:{方法标识或自定义key}:{用户名}
     * <p>
     * 不同用户的请求计数独立，互不影响
     */
    private String buildKey(ProceedingJoinPoint point, RateLimit rateLimit) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        // 方法全名：类全限定名#方法名
        String method = signature.getDeclaringTypeName() + "#" + signature.getName();
        // 自定义 Key 优先，为空则用方法名
        String customKey = rateLimit.key().isEmpty() ? method : rateLimit.key();
        // 加上用户名，实现按用户限流
        String user = "anonymous";
        try {
            if (SecurityUtils.isAuthenticated()) {
                user = SecurityUtils.getCurrentUsername();
            }
        } catch (Exception ignored) {
        }
        return Constants.REDIS_RATE_LIMIT_PREFIX + customKey + ":" + user;
    }
}
