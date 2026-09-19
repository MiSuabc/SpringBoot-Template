package com.example.template.aspect;

import com.example.template.common.annotation.OperLog;
import com.example.template.security.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;

/**
 * 操作日志切面
 * <p>
 * 拦截标注了 @OperLog 注解的 Controller 方法，自动记录操作日志。
 * <p>
 * 记录内容：
 * - 操作人（从 SecurityContext 获取）
 * - 操作描述（注解 value 属性）
 * - 操作类型（新增/修改/删除等）
 * - 请求 IP（支持代理转发）
 * - 执行耗时
 * - 成功/失败状态
 * - 请求参数（可选）
 * <p>
 * 当前日志输出到控制台/文件，后续可扩展为写入数据库 sys_oper_log 表。
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperLogAspect {

    /** Jackson 3 序列化器，用于将方法参数转为 JSON */
    private final JsonMapper jsonMapper;

    /**
     * 环绕通知：在方法执行前后均织入逻辑
     * - 记录开始时间
     * - 执行目标方法
     * - 无论成功或失败，都记录操作日志
     */
    @Around("@annotation(operLog)")
    public Object around(ProceedingJoinPoint point, OperLog operLog) throws Throwable {
        long start = System.currentTimeMillis();

        Object result;
        Exception ex = null;
        try {
            // 执行目标方法
            result = point.proceed();
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            // 无论成功失败，都保存操作日志
            try {
                saveLog(point, operLog, start, ex);
            } catch (Exception e) {
                log.error("[操作日志] 保存失败", e);
            }
        }
        return result;
    }

    /**
     * 构建并输出操作日志
     */
    private void saveLog(ProceedingJoinPoint point, OperLog operLog, long start, Exception ex) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        // 操作描述：优先用注解中配置的值，为空则用 类名.方法名
        String operName = operLog.value().isEmpty() ? className + "." + methodName : operLog.value();

        // 获取操作人（未登录时为 system）
        String username = "system";
        try {
            if (SecurityUtils.isAuthenticated()) {
                username = SecurityUtils.getCurrentUsername();
            }
        } catch (Exception ignored) {
        }

        // 获取请求 IP
        String ip = getRequestIp();
        // 计算耗时
        long costTime = System.currentTimeMillis() - start;

        log.info("[操作日志] 操作人={}, 操作={}, 类型={}, IP={}, 耗时={}ms, 状态={}",
                username, operName, operLog.type().getDescription(),
                ip, costTime, ex == null ? "成功" : "失败");

        // 如果注解配置了记录请求参数
        if (operLog.saveRequestData()) {
            try {
                String args = jsonMapper.writeValueAsString(point.getArgs());
                log.info("[操作日志] 请求参数: {}", args);
            } catch (Exception e) {
                log.debug("[操作日志] 参数序列化失败", e);
            }
        }
    }

    /**
     * 获取客户端真实 IP
     * 优先级：X-Forwarded-For > X-Real-IP > RemoteAddr
     * 支持反向代理（Nginx）场景
     */
    private String getRequestIp() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return "unknown";
            HttpServletRequest request = attrs.getRequest();

            // Nginx 反向代理会在 X-Forwarded-For 中携带真实 IP
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Real-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            return ip;
        } catch (Exception e) {
            return "unknown";
        }
    }
}
