package com.jy.crypto.system.script.controller;

import com.jy.crypto.system.script.facade.dto.ScriptDto;
import com.jy.crypto.system.script.facade.enums.ScriptLanguage;
import com.jy.crypto.system.script.service.ScriptReadService;
import com.jy.crypto.system.script.service.ScriptWriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("script")
public class ScriptController {

    private final ScriptReadService readService;
    private final ScriptWriteService writeService;

    @GetMapping("{id}")
    public ScriptDto get(@PathVariable Long id) {
        return readService.getById(id);
    }

    @PostMapping
    public Long add(@RequestBody @Valid ScriptDto dto) {
        return writeService.add(dto);
    }
}
