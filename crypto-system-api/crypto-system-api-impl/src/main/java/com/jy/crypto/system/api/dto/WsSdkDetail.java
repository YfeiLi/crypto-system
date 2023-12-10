package com.jy.crypto.system.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class WsSdkDetail extends ApiSdkListItem {
    // 连接哈希值脚本
    private Long connectHashCodeScriptId;
    // url构造脚本
    private Long urlGenerateScriptId;
    // 生成订阅消息脚本
    private Long subscribeMsgGenerateScriptId;
    // 生成取消订阅消息脚本
    private Long unsubscribeMsgGenerateScriptId;
    // 消息接收过滤脚本
    private Long msgReceiveFilterScriptId;
}
