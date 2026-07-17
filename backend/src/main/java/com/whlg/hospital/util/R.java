package com.whlg.hospital.util;

import java.io.Serializable;

public class R<T> implements Serializable {

    private Integer code;
    private String message;
    private T data;

    public R() {
    }

    public R(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> R<T> ok(T data) {
        return new R<T>(StatusCode.OK, "success", data);
    }

    public static <T> R<T> ok() {
        return new R<T>(StatusCode.OK, "success", null);
    }

    public static <T> R<T> fail(Integer code, String message) {
        return new R<T>(code, message, null);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}