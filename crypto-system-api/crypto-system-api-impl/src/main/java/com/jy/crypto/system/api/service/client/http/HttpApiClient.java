package com.jy.crypto.system.api.service.client.http;

import com.jy.crypto.system.api.dto.HttpApiDetail;
import com.jy.crypto.system.api.dto.HttpSdkDetail;
import com.jy.crypto.system.api.facade.dto.HttpResult;
import com.jy.crypto.system.api.service.ApiReadService;
import com.jy.crypto.system.api.service.ApiSdkReadService;
import com.jy.crypto.system.infrastructure.exception.BusinessException;
import com.jy.crypto.system.infrastructure.exception.ErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class HttpApiClient {

    private final Map<String, HttpSdkClient> sdkClientMap = new ConcurrentHashMap<>();

    private final ApplicationContext applicationContext;
    private final ApiReadService apiReadService;
    private final ApiSdkReadService apiSdkReadService;

    /**
     * 初始化sdk
     */
    @PostConstruct
    public void init() {
        List<HttpSdkDetail> sdkDetailList = apiSdkReadService.getAllHttpSdkDetail();
        for (HttpSdkDetail httpSdkDetail : sdkDetailList) {
            loadSdk(httpSdkDetail);
        }
    }

    /**
     * 请求api
     */
    public HttpResult invoke(String code, Map<String, Object> params) {
        HttpApiDetail apiDetail = apiReadService.getHttpApiDetail(code);
        HttpSdkClient sdkClient = sdkClientMap.get(apiDetail.getSdkCode());
        if (sdkClient == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "sdk(code=" + apiDetail.getSdkCode() + ")");
        }
        return sdkClient.invoke(apiDetail, params);
    }

    /**
     * 加载sdk
     */
    public void loadSdk(HttpSdkDetail sdkDetail) {
        HttpSdkClient httpSdkClient = applicationContext.getBean(HttpSdkClient.class, sdkDetail);
        sdkClientMap.put(sdkDetail.getCode(), httpSdkClient);
        log.info("load sdk({}) success", sdkDetail.getCode());
    }

    /**
     * 移除sdk
     */
    public void removeSdk(String sdkCode) {
        sdkClientMap.remove(sdkCode);
    }
}
