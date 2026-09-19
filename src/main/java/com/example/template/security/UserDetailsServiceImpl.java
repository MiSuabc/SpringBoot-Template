package com.example.template.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 用户认证实现类
 * <p>
 * Spring Security 通过此类的 loadUserByUsername 方法加载用户信息，
 * 用于登录认证和 JWT 过滤器中的用户加载。
 * <p>
 * 当前为硬编码示例（admin 用户），实际项目应从数据库查询。
 * 替换为注入 UserService 查库即可：
 * <pre>
 * SysUser user = userService.lambdaQuery().eq(SysUser::getUsername, username).one();
 * if (user == null) throw new UsernameNotFoundException("用户不存在");
 * return User.builder()
 *     .username(user.getUsername())
 *     .password(user.getPassword())
 *     .authorities(roleService.selectRolesByUserId(user.getId()))
 *     .build();
 * </pre>
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * 根据用户名加载用户信息
     *
     * @param username 用户名
     * @return UserDetails 用户详情（含密码、权限等）
     * @throws UsernameNotFoundException 用户不存在时抛出
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO: 替换为数据库查询
        if ("admin".equals(username)) {
            return User.builder()
                    .username("admin")
                    // BCrypt 加密的密码（明文：admin123）
                    .password("$2a$10$N.zmdr9k7uOCQbU4klcKpe.bKqWi3Ihc3SLnXk5jQ7gQ2uqRC.2")
                    .authorities("ROLE_admin")
                    .accountExpired(false)     // 账号未过期
                    .accountLocked(false)     // 账号未锁定
                    .credentialsExpired(false) // 凭证未过期
                    .disabled(false)          // 账号未禁用
                    .build();
        }
        throw new UsernameNotFoundException("用户不存在: " + username);
    }
}