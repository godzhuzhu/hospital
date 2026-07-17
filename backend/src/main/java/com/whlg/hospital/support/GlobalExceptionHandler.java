package com.whlg.hospital.support;

import com.whlg.hospital.util.R;
import com.whlg.hospital.util.StatusCode;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public R<Object> handleApiException(ApiException ex) {
        return R.fail(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Object> handleValidationException(MethodArgumentNotValidException ex) {
        return R.fail(StatusCode.BAD_REQUEST, ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler(Exception.class)
    public R<Object> handleException(Exception ex) {
        return R.fail(StatusCode.ERROR, ex.getMessage() == null ? "服务器异常" : ex.getMessage());
    }
}