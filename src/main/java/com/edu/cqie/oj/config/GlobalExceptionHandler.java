package com.edu.cqie.oj.config;

import com.edu.cqie.oj.domain.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SecurityException.class)
    public Result<?> handleSecurityException(SecurityException e) {
        log.warn("安全异常: {}", e.getMessage());
        return Result.error(403, e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: {}", e.getMessage(), e);
        if (e.getCause() instanceof SecurityException) {
            return Result.error(403, e.getCause().getMessage());
        }
        if (e.getMessage() != null && e.getMessage().contains("SQL注入")) {
            return Result.error(400, e.getMessage());
        }
        return Result.error("操作失败: " + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return Result.error("服务器内部错误，请联系管理员");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("参数异常: {}", e.getMessage());
        return Result.error(400, e.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public Result<?> handleNullPointerException(NullPointerException e) {
        log.error("空指针异常: {}", e.getMessage(), e);
        return Result.error("系统繁忙，请稍后重试");
    }
}
