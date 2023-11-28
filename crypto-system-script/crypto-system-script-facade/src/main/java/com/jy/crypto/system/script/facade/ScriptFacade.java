package com.jy.crypto.system.script.facade;

import java.util.Map;

public interface ScriptFacade {

    Object execute(Long id, Map<String, Object> variables);
}
