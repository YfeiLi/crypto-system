package com.jy.crypto.system.api.dto;

import com.jy.crypto.system.api.facade.enums.ApiType;
import lombok.Data;

@Data
public class ApiSdkListItem {
    private String code;
    private ApiType type;
    private String exchange;
    private String description;
    private String baseUrl;
}
