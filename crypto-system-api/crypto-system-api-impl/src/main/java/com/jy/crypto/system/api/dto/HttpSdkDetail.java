package com.jy.crypto.system.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class HttpSdkDetail extends ApiSdkListItem {
    private Long requestHandlerScriptId;
    private Long responseHandlerScriptId;
}
