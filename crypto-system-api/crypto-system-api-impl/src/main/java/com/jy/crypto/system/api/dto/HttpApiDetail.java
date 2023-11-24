package com.jy.crypto.system.api.dto;

import com.jy.crypto.system.api.facade.enums.HttpMethod;
import com.jy.crypto.system.api.facade.enums.HttpParamType;
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
    private List<Param> paramList;

    @Data
    public static class Param {
        private String name;
        private Boolean required;
        private HttpParamType type;
        private Integer lengthLimit;
    }
}
