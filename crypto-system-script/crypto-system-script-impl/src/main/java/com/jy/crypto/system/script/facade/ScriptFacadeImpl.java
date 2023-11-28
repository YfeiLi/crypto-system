package com.jy.crypto.system.script.facade;

import com.jy.crypto.system.script.service.ScriptExecuteService;
import com.jy.crypto.system.script.service.ScriptReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class ScriptFacadeImpl implements ScriptFacade {

    private final ScriptReadService readService;
    private final ScriptExecuteService executeService;

    @Override
    public Object execute(Long id, Map<String, Object> variables) {
        return executeService.execute(readService.getById(id), variables);
    }
}
