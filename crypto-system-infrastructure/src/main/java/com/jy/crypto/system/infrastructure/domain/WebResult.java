package com.jy.crypto.system.infrastructure.domain;

import com.jy.crypto.system.infrastructure.exception.ErrorCode;
import lombok.Data;

@Data
public class WebResult {
    private String code;
    private String message;
    private Object data;

    public WebResult() {
        this.code = "SUCCESS";
    }

    public WebResult(Object data) {
        this.code = "SUCCESS";
        this.data = data;
    }

    public WebResult(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
