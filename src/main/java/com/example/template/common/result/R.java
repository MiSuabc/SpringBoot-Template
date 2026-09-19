package com.example.template.common.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一响应结果封装类
 * <p>
 * 所有 Controller 接口统一返回 R<T>，前端只需按固定结构解析：
 * <pre>
 * {"code": 200, "msg": "操作成功", "data": {...}}
 * </pre>
 * <p>
 * 使用示例：
 * - R.ok()                     → 成功，无数据
 * - R.ok(userVO)               → 成功，携带数据
 * - R.fail("用户名已存在")     → 失败，自定义消息
 * - R.fail(ResultCode.NOT_FOUND) → 失败，使用预定义状态码
 *
 * @param <T> 响应数据类型
 */
@Data
@Schema(description = "统一响应结果")
public class R<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 状态码，200 表示成功 */
    @Schema(description = "状态码")
    private int code;

    /** 提示消息 */
    @Schema(description = "提示消息")
    private String msg;

    /** 响应数据 */
    @Schema(description = "响应数据")
    private T data;

    // ==================== 成功响应 ====================

    /** 成功（无数据） */
    public static <T> R<T> ok() {
        return ok(null);
    }

    /** 成功（携带数据） */
    public static <T> R<T> ok(T data) {
        return ok(ResultCode.SUCCESS.getMessage(), data);
    }

    /** 成功（自定义消息 + 数据） */
    public static <T> R<T> ok(String msg, T data) {
        R<T> r = new R<>();
        r.setCode(ResultCode.SUCCESS.getCode());
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    // ==================== 失败响应 ====================

    /** 失败（默认消息） */
    public static <T> R<T> fail() {
        return fail(ResultCode.FAIL.getMessage());
    }

    /** 失败（自定义消息） */
    public static <T> R<T> fail(String msg) {
        return fail(ResultCode.FAIL.getCode(), msg);
    }

    /** 失败（自定义状态码 + 消息） */
    public static <T> R<T> fail(int code, String msg) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

    /** 失败（使用预定义状态码枚举） */
    public static <T> R<T> fail(ResultCode resultCode) {
        return fail(resultCode.getCode(), resultCode.getMessage());
    }

    /** 判断是否成功 */
    public boolean isSuccess() {
        return this.code == ResultCode.SUCCESS.getCode();
    }
}