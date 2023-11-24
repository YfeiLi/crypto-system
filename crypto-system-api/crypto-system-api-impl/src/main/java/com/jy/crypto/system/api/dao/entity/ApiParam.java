package com.jy.crypto.system.api.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.jy.crypto.system.api.facade.enums.HttpParamType;
import lombok.Data;

/**
 * 主键(apiCode,name)
 */
@Data
public class ApiParam {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long apiId;
    private String name;
    private Boolean required;
    private HttpParamType type;
    private Integer lengthLimit;
}
