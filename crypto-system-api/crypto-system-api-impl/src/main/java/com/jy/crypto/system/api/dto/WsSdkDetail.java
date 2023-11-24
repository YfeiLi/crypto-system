package com.jy.crypto.system.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class WsSdkDetail extends ApiSdkListItem {
    private Boolean multiConnect;
    private Boolean needPath;
    private Boolean needEvent;
    // 订阅处理器，groovy代码
    private String subscribeHandler;
    // 取消订阅处理器，groovy代码
    private String unsubscribeHandler;
    // 接收处理器，groovy代码
    private String receiveHandler;
}
