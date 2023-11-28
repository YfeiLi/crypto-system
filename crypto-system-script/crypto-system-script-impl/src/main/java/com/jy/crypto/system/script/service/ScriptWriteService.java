package com.jy.crypto.system.script.service;

import com.jy.crypto.system.script.convert.ScriptConverter;
import com.jy.crypto.system.script.dao.entity.Script;
import com.jy.crypto.system.script.dao.mapper.ScriptMapper;
import com.jy.crypto.system.script.facade.dto.ScriptDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ScriptWriteService {

    private final ScriptMapper mapper;
    private final ScriptConverter converter;

    public Long add(ScriptDto scriptDto) {
        Script entity = converter.toEntity(scriptDto);
        mapper.insert(entity);
        return entity.getId();
    }
}
