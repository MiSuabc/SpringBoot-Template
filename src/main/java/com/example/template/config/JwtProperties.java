package com.example.template.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性绑定类
 * <p>
 * 将 application.yml 中以 jwt 为前缀的配置项自动绑定到此类字段：
 * <pre>
 * jwt:
 *   secret: abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ  # 密钥（至少 64 字符）
 *   expire: 86400        # Token 有效期（秒），默认 24 小时
 *   refresh-expire: 604800  # 刷新 Token 有效期（秒），默认 7 天
 * </pre>
 * 使用 @ConfigurationProperties 而非 @Value，可获得 IDE 提示和类型安全。
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /** JWT 签名密钥，用于 HMAC-SHA 算法，长度至少 64 字符 */
    private String secret = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /** Token 有效期（秒），默认 86400 = 24 小时 */
    private long expire = 86400;

    /** 刷新 Token 有效期（秒），默认 604800 = 7 天 */
    private long refreshExpire = 604800;
}