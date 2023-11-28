package com.jy.crypto.system.script.facade.dto;

import com.jy.crypto.system.script.facade.enums.ScriptLanguage;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScriptDto {
    private Long id;
    @NotNull
    private ScriptLanguage language;
    @NotEmpty
    private String content;
}
