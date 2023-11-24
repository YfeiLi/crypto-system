package com.jy.crypto.system.infrastructure.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode, Object ...args) {
        super(String.format(errorCode.getMsg(), args));
        this.errorCode = errorCode;
    }

    public BusinessException(Throwable cause, ErrorCode errorCode, Object ...args) {
        super(String.format(errorCode.getMsg(), args));
        this.errorCode = errorCode;
        this.initCause(cause);
    }
}
