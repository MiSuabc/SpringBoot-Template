package com.example.template.security;

import com.example.template.common.constant.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器
 * <p>
 * 继承 OncePerRequestFilter，保证每个请求只执行一次。
 * <p>
 * 工作流程：
 * 1. 从请求头 Authorization 中提取 JWT Token
 * 2. 检查 Token 是否过期
 * 3. 检查 Token 是否在 Redis 中（支持主动登出/踢人）
 * 4. 解析 Token 获取用户名，通过 UserDetailsService 加载用户信息
 * 5. 构建 Authentication 对象存入 SecurityContext，后续即可通过 SecurityUtils 获取当前用户
 * <p>
 * 如果 Token 无效或不存在，不拦截请求，交给后续的 Security 过滤链处理（最终返回 401）。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** JWT 工具类 */
    private final JwtUtils jwtUtils;

    /** 用户详情服务，用于根据用户名加载用户信息和权限 */
    private final UserDetailsService userDetailsService;

    /** Redis 操作，用于检查 Token 是否有效（未登出） */
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 1. 从请求头解析 Token
        String token = resolveToken(request);

        if (StringUtils.hasText(token)) {
            try {
                // 2. 检查 Token 是否过期
                if (jwtUtils.isTokenExpired(token)) {
                    log.warn("[JWT] Token 已过期");
                    chain.doFilter(request, response);
                    return;
                }

                // 3. 从 Token 中提取用户名
                String username = jwtUtils.getUsernameFromToken(token);

                // 4. 检查 Token 是否在 Redis 中（用户可能已登出或被管理员踢下线）
                String redisKey = Constants.REDIS_TOKEN_PREFIX + username;
                if (Boolean.FALSE.equals(redisTemplate.hasKey(redisKey))) {
                    log.warn("[JWT] Token 不在 Redis 中, 可能已登出");
                    chain.doFilter(request, response);
                    return;
                }

                // 5. 如果 SecurityContext 中还没有认证信息，则加载并设置
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // 构建认证 Token，包含用户信息和权限列表
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 存入 SecurityContext，后续请求中可通过 SecurityUtils 获取
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                log.error("[JWT] Token 解析失败: {}", e.getMessage());
            }
        }

        // 继续执行后续过滤器
        chain.doFilter(request, response);
    }

    /**
     * 从请求头中解析 Token
     * 请求头格式：Authorization: Bearer xxx.yyy.zzz
     */
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader(Constants.JWT_TOKEN_HEADER);
        if (StringUtils.hasText(bearer) && bearer.startsWith(Constants.JWT_TOKEN_PREFIX)) {
            // 去掉 "Bearer " 前缀，返回纯 Token
            return bearer.substring(Constants.JWT_TOKEN_PREFIX.length());
        }
        return null;
    }
}
