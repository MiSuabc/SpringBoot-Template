package com.example.template.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置类
 * <p>
 * Spring Boot 默认的 RedisTemplate 使用 JDK 序列化，
 * 存储在 Redis 中是乱码（二进制格式），不利于调试和跨语言交互。
 * <p>
 * 本配置自定义序列化方案：
 * - Key: StringRedisSerializer（普通字符串，可读性好）
 * - Value: GenericJacksonJsonRedisSerializer（JSON 格式，带类型信息，支持反序列化为原对象）
 * <p>
 * 注意：Spring Data Redis 4.0 从 Jackson 2 迁移到 Jackson 3，
 * 旧类 GenericJackson2JsonRedisSerializer 已废弃，使用 GenericJacksonJsonRedisSerializer 替代。
 */
@Configuration
public class RedisConfig {

    /**
     * 自定义 RedisTemplate，用于操作对象类型的数据
     * - Key 用 String 序列化
     * - Value 用 JSON 序列化（带类型信息，可自动反序列化）
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Key 和 HashKey 用 String 序列化，保证可读性
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        // Value 和 HashValue 用 JSON 序列化，支持对象存储
        GenericJacksonJsonRedisSerializer jsonSerializer = GenericJacksonJsonRedisSerializer.builder().build();

        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * StringRedisTemplate，专用于 String 类型操作
     * Spring Boot 默认已提供此 Bean，此处显式声明确保配置一致
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }
}
