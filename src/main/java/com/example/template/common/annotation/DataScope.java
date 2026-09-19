package com.example.template.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限注解
 * <p>
 * 标注在 Service 或 Mapper 方法上，用于实现数据级别的权限控制。
 * <p>
 * 数据权限与接口权限不同：
 * - 接口权限：控制能否访问某个接口（如 sys:user:list）
 * - 数据权限：控制能查到哪些数据（如只能看到本部门的数据）
 * <p>
 * 配合 DataScopeAspect 切面使用，切面会自动在 SQL 查询条件中拼接部门/用户过滤。
 * <p>
 * 使用示例：
 * <pre>
 * // 查询用户列表时自动按部门过滤
 * &#64;DataScope(deptAlias = "d")
 * public List&lt;SysUser&gt; selectUserList(SysUser user) { ... }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataScope {

    /** 部门表别名，用于 SQL 拼接，默认为 "d" */
    String deptAlias() default "d";

    /** 用户表别名，用于 SQL 拼接，默认为 "u" */
    String userAlias() default "u";

    /** 是否仅过滤当前用户自己的数据（true 时忽略部门，仅过滤 userId） */
    boolean isUser() default false;
}

