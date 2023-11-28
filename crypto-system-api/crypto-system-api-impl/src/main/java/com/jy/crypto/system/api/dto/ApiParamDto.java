package com.jy.crypto.system.api.dto;

import com.jy.crypto.system.api.facade.enums.ParamType;
import lombok.Data;

import java.util.Optional;

@Data
public class ApiParamDto {
    private Long id;
    private String name;
    private Boolean required;
    private ParamType type;
    private Integer lengthLimit;

    public Optional<String> validate(Object param) {
        if (param == null || "".equals(param)) {
            if (required) {
                return Optional.of("required");
            }
            return Optional.empty();
        }
        if (lengthLimit != null && lengthLimit > 0) {
            if (param.toString().length() > lengthLimit) {
                return Optional.of("length limit " + lengthLimit);
            }
        }
        if (ParamType.NUM.equals(type)) {
            if (!param.toString().matches("-?\\d+(\\.\\d+)?")) {
                return Optional.of("require number");
            }
        }
        if (ParamType.BOOL.equals(type)) {
            if (!param.toString().equals("true") && !param.toString().equals("false")) {
                return Optional.of("require boolean");
            }
        }
        if (ParamType.STR.equals(type)) {
            if (!(param instanceof String)) {
                return Optional.of("require string");
            }
        }
        return Optional.empty();
    }
}
