package com.jy.crypto.system.api.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class WsSubscribeRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String apiCode;
    private Long accountId;
}
