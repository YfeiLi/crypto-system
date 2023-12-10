package com.jy.crypto.system.api.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.util.Map;

@Data
public class HttpSdkConfig {
    @TableId(type = IdType.INPUT)
    private Long id;
    private String sdkCode;
    private Long timeout;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, String> headers;
    // 请求构造脚本
    private Long requestGenerateScriptId;
    // 响应处理脚本
    private Long responseHandleScriptId;
}
