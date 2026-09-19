package com.example.template.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring Web MVC 配置类
 * <p>
 * 实现 WebMvcConfigurer 接口可重写以下方法：
 * - addInterceptors: 添加拦截器
 * - addResourceHandlers: 静态资源映射
 * - configureMessageConverters: 自定义消息转换器
 * - addArgumentResolvers: 自定义参数解析器
 * <p>
 * 当前为空实现，作为扩展入口保留。
 * 跨域已在 SecurityConfig 中配置，此处无需重复。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

}
