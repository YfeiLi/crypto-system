package com.jy.crypto.system.api.facade.dto;

import lombok.Data;

@Data
public class HttpResult {
    private Boolean success;
    private Object data;
    private String msg;
}
