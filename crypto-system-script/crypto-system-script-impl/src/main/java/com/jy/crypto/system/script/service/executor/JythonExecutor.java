package com.jy.crypto.system.script.service.executor;

import org.python.util.PythonInterpreter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class JythonExecutor {

    public Object execute(String script, Map<String, Object> variables) {
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.exec("print('Hello python')");
        return null;
    }

    public static void main(String[] args) {
        try (PythonInterpreter interpreter = new PythonInterpreter()) {
            Map<String, Object> map = new HashMap<>();
            map.put("c", 2);
            interpreter.set("test", map);
            interpreter.exec("map.put(\"a\", 2)");
            System.out.println(map);
        }
    }
}
