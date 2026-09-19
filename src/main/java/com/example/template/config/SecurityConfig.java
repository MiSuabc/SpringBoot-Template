package com.example.template.config;

import com.example.template.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


/**
 * Spring Security 安全配置类
 * <p>
 * 核心策略：
 * 1. 关闭 CSRF（前后端分离不需要）
 * 2. 开启 CORS 跨域支持
 * 3. 无状态会话（STATELESS），完全基于 JWT 认证，不创建 HttpSession
 * 4. 白名单路径（登录、接口文档、Actuator 等）无需认证
 * 5. 其他所有请求需要 JWT 认证
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** JWT 认证过滤器，从请求头解析 Token 并设置认证信息 */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * 配置 Security 过滤链
     * 定义哪些路径需要认证、哪些路径放行、JWT 过滤器插入位置等
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 关闭 CSRF 防护：前后端分离项目使用 JWT，无需 CSRF Token
                .csrf(csrf -> csrf.disable())
                // 开启 CORS 跨域支持，使用下方自定义的跨域配置
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 无状态会话：不创建和使用 HttpSession，每次请求都通过 JWT 认证
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // OPTIONS 预检请求全部放行（CORS 需要）
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 登录接口放行
                        .requestMatchers("/auth/**").permitAll()
                        // 接口文档相关路径放行
                        .requestMatchers("/doc.html", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        // Actuator 监控端点放行（生产环境可改为认证访问）
                        .requestMatchers("/actuator/**").permitAll()
                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()
                )
                // 将 JWT 过滤器插入到 Spring Security 默认的用户名密码过滤器之前
                // 这样每个请求先经过 JWT 认证，认证通过后 SecurityContext 中就有用户信息
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /** 暴露 AuthenticationManager，供登录接口使用 */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 密码编码器
     * 使用 BCrypt 加密，每次加密生成不同的盐值，安全性高
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * CORS 跨域配置
     * 允许所有来源、所有 HTTP 方法、所有请求头，适合前后端分离开发
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));          // 允许所有来源
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")); // 允许所有方法
        config.setAllowedHeaders(List.of("*"));                 // 允许所有请求头
        config.setAllowCredentials(true);                       // 允许携带 Cookie
        config.setMaxAge(3600L);                                // 预检请求缓存 1 小时

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
