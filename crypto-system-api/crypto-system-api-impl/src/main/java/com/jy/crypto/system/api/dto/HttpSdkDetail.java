package com.jy.crypto.system.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class HttpSdkDetail extends ApiSdkListItem {
    private Long timeout;
    private Map<String, String> headers;
    private Long requestHandlerScriptId;
    private Long responseHandlerScriptId;
}
