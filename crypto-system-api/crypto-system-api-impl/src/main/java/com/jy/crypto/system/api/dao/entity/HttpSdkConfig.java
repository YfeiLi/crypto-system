package com.jy.crypto.system.api.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class HttpSdkConfig {
    @TableId(type = IdType.INPUT)
    private Long id;
    private String sdkCode;
    // 前置处理器，groovy代码
    private String beforeHandler;
    // 后置处理器，groovy代码
    private String afterHandler;
}
