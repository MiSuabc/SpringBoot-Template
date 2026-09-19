package com.example.template.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口限流注解
 * <p>
 * 标注在 Controller 方法上，由 RateLimitAspect 切面通过 Redis + Lua 脚本实现分布式限流。
 * <p>
 * 原理：在指定时间窗口内，限制每个用户（或 IP）对某接口的最大调用次数。
 * 超过限制则抛出 BusinessException，由全局异常处理器返回 429 状态码。
 * <p>
 * 使用示例：
 * <pre>
 * // 60 秒内最多调用 5 次登录接口
 * &#64;RateLimit(key = "login", time = 60, count = 5)
 * &#64;PostMapping("/login")
 * public R&lt;String&gt; login(&#64;RequestBody LoginDTO dto) { ... }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /** 限流 Key，为空时自动使用方法全名。可自定义以便多接口共享同一限流计数 */
    String key() default "";

    /** 时间窗口（秒），默认 60 秒 */
    int time() default 60;

    /** 时间窗口内允许的最大请求次数，默认 10 次 */
    int count() default 10;

    /** 超过限流时的提示消息 */
    String message() default "请求过于频繁，请稍后再试";
}