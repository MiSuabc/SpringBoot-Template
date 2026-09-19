package com.example.template.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring 上下文工具类
 * <p>
 * 在非 Spring 管理的类中（如普通工具类、枚举、多线程等）获取 Bean 或配置。
 * 通过实现 ApplicationContextAware 接口，在 Spring 启动时自动注入上下文。
 * <p>
 * 使用示例：
 * <pre>
 * // 在普通类中获取 Bean
 * UserService userService = SpringUtils.getBean(UserService.class);
 *
 * // 获取配置文件中的属性
 * String secret = SpringUtils.getProperty("jwt.secret");
 *
 * // 获取当前激活的 Profile
 * String profile = SpringUtils.getActiveProfile(); // "dev" / "prod"
 * </pre>
 */
@Component
public class SpringUtils implements ApplicationContextAware {

    /** Spring 应用上下文，在 setApplicationContext 时赋值 */
    private static ApplicationContext applicationContext;

    /**
     * Spring 启动时自动调用，注入应用上下文
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtils.applicationContext = applicationContext;
    }

    /**
     * 按类型获取 Bean
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 按名称 + 类型获取 Bean
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }

    /**
     * 获取 Spring 应用上下文
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 获取配置文件中的属性值
     */
    public static String getProperty(String key) {
        return applicationContext.getEnvironment().getProperty(key);
    }

    /**
     * 获取配置文件中的属性值，带默认值
     */
    public static String getProperty(String key, String defaultValue) {
        return applicationContext.getEnvironment().getProperty(key, defaultValue);
    }

    /**
     * 获取当前激活的环境名称（dev/test/prod）
     */
    public static String getActiveProfile() {
        String[] profiles = applicationContext.getEnvironment().getActiveProfiles();
        return profiles.length > 0 ? profiles[0] : "default";
    }
}
