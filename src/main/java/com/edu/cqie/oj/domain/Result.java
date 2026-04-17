package com.edu.cqie.oj.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    public Result() {
    }

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    public static <T> Result<T> success(String msg) {
        return new Result<>(200, msg, null);
    }

    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(200, msg, data);
    }

    public static <T> Result<T> error() {
        return new Result<>(500, "操作失败", null);
    }

    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }

    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    public static <T> Result<T> error(Integer code, String msg, T data) {
        return new Result<>(code, msg, data);
    }

    @JsonIgnore
    public boolean isSuccess() {
        return code != null && code == 200;
    }

    @JsonIgnore
    public boolean isError() {
        return !isSuccess();
    }

    public Result<T> put(String key, Object value) {
        if (data == null) {
            data = (T) new java.util.HashMap<String, Object>();
        }
        if (data instanceof java.util.Map) {
            ((java.util.Map<String, Object>) data).put(key, value);
        }
        return this;
    }
}