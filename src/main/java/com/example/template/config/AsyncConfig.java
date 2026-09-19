package com.example.template.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步任务线程池配置类
 * <p>
 * 配合 @Async 注解使用，为不同业务场景提供独立线程池，避免相互影响。
 * <p>
 * 线程池参数说明：
 * - corePoolSize: 核心线程数（常驻线程）
 * - maxPoolSize: 最大线程数（队列满后创建至该数量）
 * - queueCapacity: 任务队列容量（核心线程满后，任务先进队列）
 * - keepAliveSeconds: 非核心线程空闲存活时间（超时回收）
 * - rejectedExecutionHandler: 拒绝策略（队列和线程都满时的处理方式）
 * - CallerRunsPolicy: 由提交任务的线程执行，起到限流作用
 */
@Configuration
public class AsyncConfig {

    /**
     * 通用异步执行线程池
     * 适用场景：一般异步操作（如异步更新缓存、发送通知等）
     * 使用方式：@Async("asyncExecutor")
     */
    @Bean("asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);              // 核心 10 线程
        executor.setMaxPoolSize(50);               // 最大 50 线程
        executor.setQueueCapacity(200);            // 队列 200
        executor.setKeepAliveSeconds(60);          // 空闲 60 秒回收
        executor.setThreadNamePrefix("async-");    // 线程名前缀，方便日志排查
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * 邮件发送专用线程池
     * 适用场景：邮件发送等 IO 密集型任务
     * 独立线程池避免邮件发送慢影响其他异步任务
     * 使用方式：@Async("mailExecutor")
     */
    @Bean("mailExecutor")
    public Executor mailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);               // 邮件发送并发量不大，核心 2 线程
        executor.setMaxPoolSize(5);                // 最大 5 线程
        executor.setQueueCapacity(100);            // 队列 100
        executor.setThreadNamePrefix("mail-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
