package com.example.template.common.exception;

import com.example.template.common.result.ResultCode;
import lombok.Getter;

/**
 * 业务异常类
 * <p>
 * 在 Service 层遇到业务规则不满足时抛出，由全局异常处理器捕获后返回标准 R 格式。
 * <p>
 * 使用示例：
 * <pre>
 * // 自定义消息
 * throw new BusinessException("用户名已存在");
 *
 * // 使用预定义状态码
 * throw new BusinessException(ResultCode.DATA_NOT_FOUND);
 *
 * // 自定义状态码 + 消息
 * throw new BusinessException(50005, "库存不足");
 * </pre>
 * <p>
 * 优点：Service 层无需 try-catch，Controller 层也无需包装，代码更简洁。
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 异常状态码 */
    private final int code;

    /** 自定义消息，状态码默认为业务异常 */
    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.BUSINESS_ERROR.getCode();
    }

    /** 使用预定义状态码枚举 */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    /** 自定义状态码 + 消息 */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
