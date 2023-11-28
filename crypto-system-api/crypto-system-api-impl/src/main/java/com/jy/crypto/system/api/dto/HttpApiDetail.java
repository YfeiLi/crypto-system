package com.jy.crypto.system.api.dto;

import com.jy.crypto.system.api.facade.enums.HttpMethod;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class HttpApiDetail extends ApiListItem {
    private String path;
    private HttpMethod method;
    private Long cacheMills;
    private String[] ignoreCacheHitParams;
    private List<ApiParamDto> paramList;
}
