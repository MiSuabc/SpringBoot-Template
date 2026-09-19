package com.example.template.security;

import com.example.template.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类
 * <p>
 * 基于 JJWT 0.12.x API 实现 JWT Token 的生成与解析。
 * <p>
 * JWT Token 结构（三段式，以 . 分隔）：
 * - Header:  {"alg": "HS256", "typ": "JWT"}
 * - Payload: {"sub": "userId", "username": "xxx", "iat": xxx, "exp": xxx}
 * - Signature: HMAC-SHA256 签名
 * <p>
 * 流程：
 * 1. 登录成功后调用 generateToken() 生成 Token 返回给前端
 * 2. 前端后续请求携带 Authorization: Bearer {token}
 * 3. JwtAuthenticationFilter 解析 Token 并认证
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {

    /** JWT 配置（密钥、过期时间等） */
    private final JwtProperties jwtProperties;

    /** 签名密钥，由 secret 字符串生成，在 init() 中初始化 */
    private SecretKey key;

    /**
     * Bean 初始化后执行，将配置中的 secret 字符串转为 HMAC-SHA 密钥
     * 密钥至少需要 256 bit（32 字节），建议 64 字符以上
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 JWT Token
     *
     * @param userId   用户 ID（存入 subject）
     * @param username 用户名（存入自定义 claim）
     * @param extra    额外的自定义声明（如角色、权限等）
     * @return JWT Token 字符串
     */
    public String generateToken(Long userId, String username, Map<String, Object> extra) {
        Date now = new Date();
        // 过期时间 = 当前时间 + 配置的有效期（秒转毫秒）
        Date expiration = new Date(now.getTime() + jwtProperties.getExpire() * 1000);
        return Jwts.builder()
                .subject(String.valueOf(userId))  // 主体：用户 ID
                .claims(extra)                    // 额外声明
                .claim("username", username)      // 自定义声明：用户名
                .issuedAt(now)                    // 签发时间
                .expiration(expiration)           // 过期时间
                .signWith(key)                    // 签名
                .compact();                       // 压缩生成最终 Token
    }

    /**
     * 解析 JWT Token，返回 Claims（声明集合）
     * 如果 Token 无效或被篡改，会抛出异常
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)                 // 验证签名
                .build()
                .parseSignedClaims(token)         // 解析并验证
                .getPayload();                    // 获取 Payload 部分
    }

    /**
     * 判断 Token 是否已过期
     */
    public boolean isTokenExpired(String token) {
        try {
            return parseToken(token).getExpiration().before(new Date());
        } catch (Exception e) {
            // 解析失败也视为过期
            return true;
        }
    }

    /**
     * 从 Token 中提取用户名
     */
    public String getUsernameFromToken(String token) {
        return parseToken(token).get("username", String.class);
    }

    /**
     * 从 Token 中提取用户 ID
     */
    public Long getUserIdFromToken(String token) {
        return Long.parseLong(parseToken(token).getSubject());
    }
}
