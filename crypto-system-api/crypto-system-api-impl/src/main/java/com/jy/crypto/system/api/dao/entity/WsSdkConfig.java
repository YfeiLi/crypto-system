package com.jy.crypto.system.api.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class WsSdkConfig {
    @TableId(type = IdType.INPUT)
    private Long id;
    private String sdkCode;
    private Boolean multiConnect;
    private Boolean needPath;
    private Boolean needEvent;
    // 订阅处理脚本
    private Long subscribeHandlerScriptId;
    // 取消订阅处理脚本
    private Long unsubscribeHandlerScriptId;
    // 接收处理脚本
    private Long receiveHandlerScriptId;
}
