package com.jy.crypto.system.api.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.jy.crypto.system.api.facade.enums.ApiType;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ApiSdk {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private ApiType type;
    private String exchange;
    private String description;
    private String baseUrl;
    @OrderBy
    @TableField(fill = FieldFill.INSERT)
    private OffsetDateTime createTime;
    @TableField(fill = FieldFill.INSERT)
    private String createdBy;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private OffsetDateTime updateTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;
    private Integer version;
    @TableLogic
    private Boolean deleted;
}
