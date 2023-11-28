package com.jy.crypto.system.api.dto;

import com.jy.crypto.system.api.facade.enums.ParamType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class WsApiDetail extends ApiListItem {
    private String path;
    private String event;
    private List<ApiParamDto> paramList;
}
