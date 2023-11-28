package com.jy.crypto.system.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class WsSdkDetail extends ApiSdkListItem {
    private Boolean needPath;
    private Boolean needEvent;
    // 订阅处理脚本
    private Long subscribeHandlerScriptId;
    // 取消订阅处理脚本
    private Long unsubscribeHandlerScriptId;
    // 接收处理脚本
    private Long receiveHandlerScriptId;
}
