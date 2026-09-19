package com.example.template.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务操作结果类型
 * <p>
 * 用于操作日志中标记操作是正常完成还是发生了异常。
 */
@Getter
@AllArgsConstructor
public enum BusinessType {

    NORMAL("正常"),
    EXCEPTION("异常");

    /** 类型描述 */
    private final String description;
}
