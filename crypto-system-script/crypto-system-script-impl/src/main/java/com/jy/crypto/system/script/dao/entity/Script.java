package com.jy.crypto.system.script.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.jy.crypto.system.script.facade.enums.ScriptLanguage;
import lombok.Data;

@Data
public class Script {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String content;
    private ScriptLanguage language;
}
