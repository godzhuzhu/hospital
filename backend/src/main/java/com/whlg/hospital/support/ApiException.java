package com.whlg.hospital.support;

public class ApiException extends RuntimeException {

    private final Integer code;

    public ApiException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}