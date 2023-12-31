package com.jy.crypto.system.account.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class AccountGroup {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
}
