package com.example.template.security;

import com.example.template.common.constant.Constants;
import com.example.template.common.exception.BusinessException;
import com.example.template.common.result.ResultCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 安全工具类
 * <p>
 * 提供静态方法，在任何地方获取当前登录用户信息。
 * 底层从 Spring Security 的 SecurityContext 中读取认证信息。
 * <p>
 * 使用示例：
 * <pre>
 * String username = SecurityUtils.getCurrentUsername();  // 获取当前用户名
 * boolean isAdmin = SecurityUtils.isAdmin();             // 判断是否超级管理员
 * </pre>
 */
public class SecurityUtils {

    /**
     * 获取当前登录用户名
     * 如果未登录或认证已过期，抛出 BusinessException
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return String.valueOf(principal);
    }

    /**
     * 判断当前请求是否已认证
     * 排除匿名用户（anonymousUser）
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal());
    }

    /**
     * 判断当前用户是否为超级管理员
     * 通过检查角色列表中是否包含 ROLE_admin
     */
    public static boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().startsWith("ROLE_" + Constants.SUPER_ADMIN_ROLE));
    }
}
