package com.jy.crypto.system.infrastructure.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    DATA_NOT_FOUND("%s not found"),
    DATA_INCONSISTENT("%s is inconsistent with %s"),
    PARAM_ERROR("param error: %s"),
    SCRIPT_EXECUTE_ERROR("script(id=%s) execute error"),
    ;

    private final String msg;

}
