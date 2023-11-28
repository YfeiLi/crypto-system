package com.jy.crypto.system.api.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Map;

@Data
public class HttpSdkConfig {
    @TableId(type = IdType.INPUT)
    private Long id;
    private String sdkCode;
    private Long timeout;
    private Map<String, String> headers;
    // 请求处理脚本
    private Long requestHandlerScriptId;
    // 响应处理脚本
    private Long responseHandlerScriptId;
}
