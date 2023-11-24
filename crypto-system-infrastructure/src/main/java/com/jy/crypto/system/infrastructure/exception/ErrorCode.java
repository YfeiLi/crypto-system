package com.jy.crypto.system.infrastructure.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    DATA_NOT_FOUND("%s not found"),
    ;

    private final String msg;

}
