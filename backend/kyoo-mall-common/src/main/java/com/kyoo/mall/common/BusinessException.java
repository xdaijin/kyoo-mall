package com.kyoo.mall.common;

import lombok.Getter;

/**
 * 业务异常，由全局异常处理器统一转为 Result。
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        this(Result.CODE_ERROR, message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
