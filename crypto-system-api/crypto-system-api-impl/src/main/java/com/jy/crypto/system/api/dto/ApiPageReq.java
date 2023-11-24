package com.jy.crypto.system.api.dto;

import com.jy.crypto.system.infrastructure.domain.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class ApiPageReq extends PageReq {
    private String code;
    private String description;
    private String sdkCode;
}
