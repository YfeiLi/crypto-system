package com.jy.crypto.system.script.service;

import com.jy.crypto.system.infrastructure.exception.BusinessException;
import com.jy.crypto.system.infrastructure.exception.ErrorCode;
import com.jy.crypto.system.script.facade.dto.ScriptDto;
import com.jy.crypto.system.script.service.executor.GroovyExecutor;
import com.jy.crypto.system.script.service.executor.JypthonExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class ScriptExecuteService {

    private final GroovyExecutor groovyExecutor;
    private final JypthonExecutor jypthonExecutor;

    public Object execute(ScriptDto scriptDto, Map<String, Object> variables) {
        try {
            return switch (scriptDto.getLanguage()) {
                case GROOVY -> groovyExecutor.execute(scriptDto.getContent(), variables);
                case JYTHON -> jypthonExecutor.execute(scriptDto.getContent(), variables);
            };
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SCRIPT_EXECUTE_ERROR, e, scriptDto.getId());
        }
    }
}
