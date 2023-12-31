package com.jy.crypto.system.api.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.jy.crypto.system.api.facade.enums.HttpMethod;
import lombok.Data;

@Data
public class HttpApiConfig {
    @TableId(type = IdType.INPUT)
    private Long id;
    private String apiCode;
    private String path;
    private HttpMethod method;
    private Long cacheMills;
}
