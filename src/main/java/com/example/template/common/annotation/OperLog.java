package com.example.template.common.annotation;

import com.example.template.common.enums.OperType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解
 * <p>
 * 标注在 Controller 方法上，由 OperLogAspect 切面自动记录操作日志。
 * <p>
 * 使用示例：
 * <pre>
 * &#64;OperLog(value = "用户管理", type = OperType.INSERT)
 * &#64;PostMapping
 * public R&lt;Void&gt; add(&#64;RequestBody UserDTO dto) { ... }
 * </pre>
 * <p>
 * 切面会自动记录：操作人、操作名称、操作类型、请求 IP、耗时、成功/失败、请求参数。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperLog {

    /** 操作描述（如"用户管理"），为空时自动使用类名.方法名 */
    String value() default "";

    /** 操作类型（新增、修改、删除等） */
    OperType type() default OperType.OTHER;

    /** 是否记录请求参数到日志（默认开启） */
    boolean saveRequestData() default true;

    /** 是否记录响应结果到日志（默认关闭，响应可能较大） */
    boolean saveResponseData() default false;
}

