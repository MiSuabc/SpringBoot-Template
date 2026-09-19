package com.example.template.common.constant;

/**
 * 系统全局常量定义
 * <p>
 * 使用 interface 定义常量，字段默认 public static final，
 * 可直接通过 Constants.XXX 引用，无需实例化。
 */
public interface Constants {

    // ==================== JWT / Token ====================

    /** JWT Token 前缀，请求头值为 "Bearer xxx.yyy.zzz" */
    String JWT_TOKEN_PREFIX = "Bearer ";

    /** JWT Token 请求头名称 */
    String JWT_TOKEN_HEADER = "Authorization";

    // ==================== Redis Key 前缀 ====================

    /** 登录 Token 在 Redis 中的 Key 前缀，格式：login_tokens:{username} */
    String REDIS_TOKEN_PREFIX = "login_tokens:";

    /** 业务缓存在 Redis 中的 Key 前缀 */
    String REDIS_CACHE_PREFIX = "cache:";

    /** 限流计数在 Redis 中的 Key 前缀 */
    String REDIS_RATE_LIMIT_PREFIX = "rate_limit:";

    // ==================== 用户 / 权限 ====================

    /** 默认密码（新增用户时使用，用户首次登录后应修改） */
    String DEFAULT_PASSWORD = "123456";

    /** 超级管理员角色标识，拥有所有权限 */
    String SUPER_ADMIN_ROLE = "admin";

    // ==================== 通用状态 ====================

    /** 状态-正常 */
    int STATUS_NORMAL = 0;

    /** 状态-禁用 */
    int STATUS_DISABLED = 1;

    // ==================== 分页默认值 ====================

    /** 默认页码 */
    int PAGE_DEFAULT_CURRENT = 1;

    /** 默认每页条数 */
    int PAGE_DEFAULT_SIZE = 10;

    /** 每页最大条数（防止前端传过大值拖垮数据库） */
    int PAGE_MAX_SIZE = 500;
}