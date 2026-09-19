package com.example.template.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * springdoc-openapi 接口文档配置类
 * <p>
 * 启动后访问 http://localhost:8080/swagger-ui.html 查看在线接口文档
 * <p>
 * 配置内容：
 * 1. 文档基本信息（标题、描述、版本、联系方式、开源协议）
 * 2. JWT 认证方案：在 Swagger UI 中可直接输入 Token 进行接口调试
 */
@Configuration
public class SpringDocConfig {

    /**
     * 配置 OpenAPI 文档元信息和安全方案
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                // 文档基本信息
                .info(new Info()
                        .title("SpringBoot 企业级开发模板 API 文档")
                        .description("开箱即用的 SpringBoot 企业级项目模板接口文档")
                        .version("1.0.0")
                        .contact(new Contact().name("开发团队").email("dev@example.com"))
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0"))
                )
                // 全局安全要求：所有接口默认需要 Bearer Token 认证
                .addSecurityItem(new SecurityRequirement().addList("Bearer"))
                // 定义安全方案：HTTP Bearer + JWT
                .components(new Components()
                        .addSecuritySchemes("Bearer", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)     // 类型为 HTTP 认证
                                .scheme("bearer")                   // Bearer 认证方案
                                .bearerFormat("JWT")                // Token 格式为 JWT
                                .in(SecurityScheme.In.HEADER)       // 通过请求头传递
                                .name("Authorization")              // 请求头名称
                        )
                );
    }
}
