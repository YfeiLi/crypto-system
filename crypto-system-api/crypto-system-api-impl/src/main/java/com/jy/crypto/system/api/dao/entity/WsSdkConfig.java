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
    // 订阅处理器，groovy代码
    private String subscribeHandler;
    // 取消订阅处理器，groovy代码
    private String unsubscribeHandler;
    // 接收处理器，groovy代码
    private String receiveHandler;
}
