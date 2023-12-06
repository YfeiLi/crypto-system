package com.jy.crypto.system.api.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class WsSdkConfig {
    @TableId(type = IdType.INPUT)
    private Long id;
    private String sdkCode;
    // 连接哈希值脚本
    private Long connectHashCodeScriptId;
    // url构造脚本
    private Long urlGenerateScriptId;
    // 生成订阅消息脚本
    private Long subscribeMsgGenerateScriptId;
    // 生成取消订阅消息脚本
    private Long unsubscribeMsgGenerateScriptId;
    // 发布路由脚本
    private Long publishRouterScriptId;
}
