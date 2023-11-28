package com.jy.crypto.system.api.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class HttpInvokeRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long apiCode;
    private Long accountId;
    private String url;
    private String header;
    private String requestBody;
    private Integer statusCode;
    private String responseBody;
    private Long durationMillis;
    private OffsetDateTime time;
}
