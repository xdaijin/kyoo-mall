package com.kyoo.mall.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应包装。code = 0 表示成功，非 0 为业务/系统错误码。
 */
@Data
public class Result<T> implements Serializable {

    public static final int CODE_SUCCESS = 0;
    public static final int CODE_UNAUTHORIZED = 401;
    public static final int CODE_FORBIDDEN = 403;
    public static final int CODE_ERROR = 500;

    private int code;
    private String message;
    private T data;

    public static <T> Result<T> ok() {
        return ok(null);
    }

    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.code = CODE_SUCCESS;
        r.message = "ok";
        r.data = data;
        return r;
    }

    public static <T> Result<T> error(String message) {
        return error(CODE_ERROR, message);
    }

    public static <T> Result<T> error(int code, String message) {
        Result<T> r = new Result<>();
        r.code = code;
        r.message = message;
        return r;
    }
}
