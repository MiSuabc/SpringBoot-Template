package com.example.template.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Redis 操作工具类
 * <p>
 * 对 RedisTemplate 的常用操作进行封装，简化调用代码。
 * <p>
 * 使用方式：在需要的地方注入 RedisUtils 即可：
 * <pre>
 * &#64;RequiredArgsConstructor
 * private final RedisUtils redisUtils;
 *
 * // 存值（带过期）
 * redisUtils.set("user:1", userVO, 30, TimeUnit.MINUTES);
 *
 * // 取值
 * UserVO user = redisUtils.get("user:1");
 *
 * // 删除
 * redisUtils.delete("user:1");
 * </pre>
 */
@Component
@RequiredArgsConstructor
public class RedisUtils {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 存入值（永久）
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 存入值（带过期时间）
     *
     * @param timeout 过期时间数值
     * @param unit    时间单位（秒/分/时等）
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 获取值
     *
     * @param key Redis Key
     * @return 值（需调用方做类型转换）
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除单个 Key
     *
     * @return true 表示删除成功
     */
    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    /**
     * 批量删除 Key
     *
     * @return 实际删除的数量
     */
    public long delete(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    /**
     * 判断 Key 是否存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 设置过期时间
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
    }

    /**
     * 获取剩余过期时间
     *
     * @return -1 表示永久，-2 表示 Key 不存在
     */
    public long getExpire(String key, TimeUnit unit) {
        Long expire = redisTemplate.getExpire(key, unit);
        return expire != null ? expire : -1;
    }

    /**
     * 自增 1（常用于计数器）
     */
    public void increment(String key) {
        redisTemplate.opsForValue().increment(key);
    }

    /**
     * 自增指定步长
     */
    public void increment(String key, long delta) {
        redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 获取旧值并设置新值（原子操作）
     */
    @SuppressWarnings("unchecked")
    public <T> T getAndSet(String key, Object value) {
        return (T) redisTemplate.opsForValue().getAndSet(key, value);
    }
}
