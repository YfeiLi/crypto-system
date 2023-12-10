package com.jy.crypto.system.data.dao.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class DataItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private String refId;
    private String key;
    private String value;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private OffsetDateTime updateTime;
}
