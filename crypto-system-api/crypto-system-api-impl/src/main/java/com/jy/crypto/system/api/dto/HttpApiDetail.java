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
    private List<ApiParamDto> paramList;

    public Boolean isCache() {
        return cacheMills != null && cacheMills > 0;
    }
}
