package com.jy.crypto.system.account.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class Account {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String exchange;
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
