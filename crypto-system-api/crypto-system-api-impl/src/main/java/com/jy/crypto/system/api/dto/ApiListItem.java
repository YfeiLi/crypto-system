package com.jy.crypto.system.api.dto;

import lombok.Data;

@Data
public class ApiListItem {
    private String code;
    private String exchange;
    private String sdkCode;
    private String description;
    private Boolean isGlobal;
}
