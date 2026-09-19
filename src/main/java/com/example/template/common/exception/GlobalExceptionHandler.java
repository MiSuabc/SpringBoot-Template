package com.example.template.common.exception;

import com.example.template.common.result.R;
import com.example.template.common.result.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * <p>
 * 统一拦截 Controller 层抛出的异常，转换为标准 R 格式响应。
 * 开发者无需在 Controller 中手写 try-catch，异常会自动被此处理器捕获。
 * <p>
 * 处理优先级：被 @ExceptionHandler 标注的方法按异常类型精确匹配，
 * 越具体的异常类型优先级越高。
 * <p>
 * 异常处理分类：
 * 1. 业务异常（BusinessException）→ 返回自定义状态码
 * 2. 参数校验异常 → 返回 400 + 校验错误详情
 * 3. 认证授权异常 → 返回 401/403
 * 4. 其他未知异常 → 返回 99999 系统异常（兜底）
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常 — 最常见的异常，Service 层主动抛出
     */
    @ExceptionHandler(BusinessException.class)
    public R<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("[业务异常] {} - {}", request.getRequestURI(), e.getMessage());
        return R.fail(e.getCode(), e.getMessage());
    }

    /**
     * @RequestBody 参数校验失败（@Valid + @RequestBody）
     * 例：DTO 中 @NotBlank、@Size 等注解校验不通过
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Void> handleValidException(MethodArgumentNotValidException e) {
        // 收集所有字段校验错误信息，用分号拼接
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("[参数校验失败] {}", message);
        return R.fail(ResultCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 表单参数绑定异常（@Valid + 表单提交，非 JSON）
     */
    @ExceptionHandler(BindException.class)
    public R<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("[参数绑定失败] {}", message);
        return R.fail(ResultCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 单参数校验失败（@Validated + @RequestParam/@PathVariable）
     * 例：@RequestParam @NotBlank String name 校验不通过
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public R<Void> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        log.warn("[约束校验失败] {}", message);
        return R.fail(ResultCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 缺少必填的 RequestParam 参数
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public R<Void> handleMissingParam(MissingServletRequestParameterException e) {
        log.warn("[缺少必填参数] {}", e.getParameterName());
        return R.fail(ResultCode.PARAM_MISSING.getCode(), "缺少必填参数: " + e.getParameterName());
    }

    /**
     * 请求方法不支持（如 POST 接口收到 GET 请求）
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<Void> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        log.warn("[请求方法不支持] {}", e.getMessage());
        return R.fail(ResultCode.METHOD_NOT_ALLOWED);
    }

    /**
     * 请求路径不存在（404）
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public R<Void> handleNotFound(NoHandlerFoundException e) {
        log.warn("[资源不存在] {}", e.getRequestURL());
        return R.fail(ResultCode.NOT_FOUND);
    }

    /**
     * 权限不足（已认证但无权限访问该资源）
     */
    @ExceptionHandler(AccessDeniedException.class)
    public R<Void> handleAccessDenied(AccessDeniedException e) {
        log.warn("[权限不足] {}", e.getMessage());
        return R.fail(ResultCode.FORBIDDEN);
    }

    /**
     * 认证失败（未登录或 Token 过期）
     */
    @ExceptionHandler(AuthenticationException.class)
    public R<Void> handleAuthentication(AuthenticationException e) {
        log.warn("[认证失败] {}", e.getMessage());
        return R.fail(ResultCode.UNAUTHORIZED);
    }

    /**
     * 兜底异常 — 所有未被上面捕获的异常，最终都会落到这里
     * 返回通用的系统异常消息，避免向用户暴露堆栈信息
     */
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("[系统异常] {} - {}", request.getRequestURI(), e.getMessage(), e);
        return R.fail(ResultCode.SYSTEM_ERROR);
    }
}
