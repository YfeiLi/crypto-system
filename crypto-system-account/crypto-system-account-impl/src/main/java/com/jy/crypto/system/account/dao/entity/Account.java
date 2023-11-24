package com.jy.crypto.system.account.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class Account {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String exchange;
    private OffsetDateTime createTime;
    private String createdBy;
    private OffsetDateTime updateTime;
    private String updatedBy;
    private Boolean deleted;
}
