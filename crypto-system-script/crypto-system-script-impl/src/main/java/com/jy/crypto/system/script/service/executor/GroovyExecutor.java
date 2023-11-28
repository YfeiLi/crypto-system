package com.jy.crypto.system.script.service.executor;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GroovyExecutor {

    public Object execute(String script, Map<String, Object> variables) {
        Binding binding = new Binding();
        for (String key : variables.keySet()) {
            binding.setProperty(key, variables.get(key));
        }
        GroovyShell shell = new GroovyShell(binding);
        return shell.evaluate(script);
    }

    public static void main(String[] args) {
        GroovyExecutor executor = new GroovyExecutor();
        String script = """
                return [a: 1, b: 2]
        """;
        Map<?, ?> result = (Map<?, ?>) executor.execute(script, Map.of("a", 1));
        Integer a = (Integer) result.get("a");
        System.out.println(a);
    }
}
