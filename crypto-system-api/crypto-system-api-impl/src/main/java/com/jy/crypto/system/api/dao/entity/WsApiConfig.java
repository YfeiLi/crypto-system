package com.jy.crypto.system.api.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class WsApiConfig {
    @TableId(type = IdType.INPUT)
    private Long id;
    private String apiCode;
    private String path;
    private String event;
}
