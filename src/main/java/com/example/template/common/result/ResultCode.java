package com.example.template.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统状态码枚举
 * <p>
 * 统一管理所有状态码，避免在代码中硬编码数字。
 * 命名规范：模块_具体错误（如 TOKEN_INVALID、DATA_NOT_FOUND）
 * <p>
 * 状态码分段：
 * - 2xx: 成功
 * - 4xx: 客户端错误（认证、权限、参数等）
 * - 5xx: 服务端错误
 * - 4xxxx: 认证相关细分（40101-40107）
 * - 5xxxx: 业务相关细分（50001-50004）
 * - 42901: 限流
 * - 99999: 系统异常兜底
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),

    // ==================== HTTP 标准状态 ====================
    UNAUTHORIZED(401, "未认证或认证已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),

    // ==================== 参数错误 ====================
    PARAM_ERROR(400, "参数校验失败"),
    PARAM_MISSING(400, "必填参数缺失"),

    // ==================== Token / 认证细分 ====================
    TOKEN_INVALID(40101, "Token 无效"),
    TOKEN_EXPIRED(40102, "Token 已过期"),
    TOKEN_MISSING(40103, "Token 缺失"),

    // ==================== 账号状态 ====================
    ACCOUNT_LOCKED(40104, "账号已被锁定"),
    ACCOUNT_DISABLED(40105, "账号已被禁用"),
    ACCOUNT_EXPIRED(40106, "账号已过期"),
    BAD_CREDENTIALS(40107, "用户名或密码错误"),

    // ==================== 业务异常 ====================
    BUSINESS_ERROR(50001, "业务异常"),
    DATA_NOT_FOUND(50002, "数据不存在"),
    DATA_EXISTS(50003, "数据已存在"),
    DATA_IN_USE(50004, "数据被占用，无法删除"),

    // ==================== 限流 ====================
    RATE_LIMIT(42901, "请求过于频繁，请稍后再试"),

    // ==================== 系统兜底 ====================
    SYSTEM_ERROR(99999, "系统异常，请联系管理员");

    /** 状态码 */
    private final int code;
    /** 提示消息 */
    private final String message;
}
