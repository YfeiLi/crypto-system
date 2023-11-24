package com.jy.crypto.system.account.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Exchange {
    @TableId(type = IdType.INPUT)
    private String code;
    private String description;
}
