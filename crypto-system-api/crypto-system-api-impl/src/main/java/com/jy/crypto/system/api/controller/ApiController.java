package com.jy.crypto.system.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jy.crypto.system.api.dto.*;
import com.jy.crypto.system.api.service.ApiSdkReadService;
import com.jy.crypto.system.api.service.ApiReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api")
public class ApiController {

    private final ApiReadService apiReadService;
    private final ApiSdkReadService apiSdkReadService;

    @GetMapping
    public IPage<ApiListItem> getApiPage(ApiPageReq req) {
        return apiReadService.getPage(req);
    }

    @GetMapping("http/{code}")
    public HttpApiDetail getHttpApiDetail(@PathVariable("code") String code) {
        return apiReadService.getHttpApiDetail(code);
    }

    @GetMapping("ws/{code}")
    public WsApiDetail getWsApiDetail(@PathVariable("code") String code) {
        return apiReadService.getWsApiDetail(code);
    }

    @GetMapping("sdk")
    public IPage<ApiSdkListItem> getApiSdkPage(ApiSdkPageReq req) {
        return apiSdkReadService.getPage(req);
    }

    @GetMapping("sdk/http/{code}")
    public HttpSdkDetail getHttpSdkDetail(@PathVariable("code") String code) {
        return apiSdkReadService.getHttpSdkDetail(code);
    }

    @GetMapping("sdk/ws/{code}")
    public WsSdkDetail getWsSdkDetail(@PathVariable("code") String code) {
        return apiSdkReadService.getWsSdkDetail(code);
    }
}
