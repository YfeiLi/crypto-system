package com.jy.crypto.system.script.service;

import com.jy.crypto.system.infrastructure.exception.BusinessException;
import com.jy.crypto.system.infrastructure.exception.ErrorCode;
import com.jy.crypto.system.script.convert.ScriptConverter;
import com.jy.crypto.system.script.dao.entity.Script;
import com.jy.crypto.system.script.dao.mapper.ScriptMapper;
import com.jy.crypto.system.script.facade.dto.ScriptDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ScriptReadService {

    private final ScriptMapper mapper;
    private final ScriptConverter converter;

    @Cacheable(value = "script", key = "#id")
    public ScriptDto getById(Long id) {
        Script script = mapper.selectById(id);
        if (script == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "script(id=" + id + ")");
        }
        return converter.toDto(script);
    }
}
