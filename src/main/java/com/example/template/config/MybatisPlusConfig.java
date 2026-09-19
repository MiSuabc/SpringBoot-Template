package com.example.template.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类
 * <p>
 * 配置内容：
 * 1. 分页插件 + 乐观锁插件
 * 2. 自动填充处理器（createTime/createBy/updateTime/updateBy 自动赋值）
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 注册 MyBatis-Plus 拦截器链
     * - PaginationInnerInterceptor: 分页插件，配置数据库类型为 MySQL
     * - OptimisticLockerInnerInterceptor: 乐观锁插件，配合 @Version 注解实现乐观锁
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件，指定数据库类型
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }

    /**
     * 自动填充处理器
     * <p>
     * 配合实体类上的 @TableField(fill = FieldFill.INSERT) 注解使用：
     * - 插入时自动填充 createTime、createBy、deleted
     * - 更新时自动填充 updateTime、updateBy
     * 免去在每个 Service 中手动 set 这些公共字段的麻烦。
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {

            /** 插入时自动填充 */
            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "createTime", java.time.LocalDateTime.class, java.time.LocalDateTime.now());
                this.strictInsertFill(metaObject, "createBy", String.class, getCurrentUsername());
            }

            /** 更新时自动填充 */
            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, "updateTime", java.time.LocalDateTime.class, java.time.LocalDateTime.now());
                this.strictUpdateFill(metaObject, "updateBy", String.class, getCurrentUsername());
            }

            /**
             * 获取当前登录用户名
             * 如果 Security 上下文中没有用户信息（如系统自动执行的任务），返回 "system"
             */
            private String getCurrentUsername() {
                try {
                    return com.example.template.security.SecurityUtils.getCurrentUsername();
                } catch (Exception e) {
                    return "system";
                }
            }
        };
    }
}
