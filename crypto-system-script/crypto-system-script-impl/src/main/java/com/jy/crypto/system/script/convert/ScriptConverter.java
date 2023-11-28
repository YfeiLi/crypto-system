package com.jy.crypto.system.script.convert;

import com.jy.crypto.system.script.dao.entity.Script;
import com.jy.crypto.system.script.facade.dto.ScriptDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ScriptConverter {

    ScriptDto toDto(Script entity);
    Script toEntity(ScriptDto dto);
}
