package com.jy.crypto.system.data.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.jy.crypto.system.data.facade.enums.DataType;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class DataKind {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private String description;
    private DataType type;
    private Long initScriptId;
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
