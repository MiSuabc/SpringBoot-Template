package com.example.template.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作类型枚举
 * <p>
 * 配合 @OperLog 注解使用，标记接口执行的操作类型，
 * 用于操作日志的分类统计和查询。
 */
@Getter
@AllArgsConstructor
public enum OperType {

    OTHER("其他"),
    INSERT("新增"),
    UPDATE("修改"),
    DELETE("删除"),
    EXPORT("导出"),
    IMPORT("导入"),
    LOGIN("登录"),
    LOGOUT("登出"),
    GRANT("授权");

    /** 操作类型描述 */
    private final String description;
}
