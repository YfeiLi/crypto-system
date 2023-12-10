package com.jy.crypto.system.api.service.client.http;

import com.jy.crypto.system.api.dto.ApiParamDto;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
@RequiredArgsConstructor
@Component
public class HttpApiClient {

    private final Map<String, HttpSdkClient> sdkClientMap = new ConcurrentHashMap<>();

    private final ApplicationContext applicationContext;
    private final ApiReadService apiReadService;
    private final ApiSdkReadService apiSdkReadService;
    private final HttpCache httpCache;

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
        // 获取api详情
        HttpApiDetail apiDetail = apiReadService.getHttpApiDetail(code);
        Future<HttpResult> future = null;
        // 检查缓存
        if (apiDetail.isCache()) {
            future = httpCache.get(code, params);
        }
        if (future == null) {
            // 调用具体实现
            future = CompletableFuture.supplyAsync(() -> invokeImpl(apiDetail, params));
            // 设置缓存
            if (apiDetail.isCache()) {
                httpCache.set(code, params, future, apiDetail.getCacheMills());
            }
        }
        // 返回结果，处理Future异常
        try {
            return future.get();
        } catch (InterruptedException e) {
            throw new BusinessException(ErrorCode.API_INVOKE_ERROR, e, code);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof BusinessException businessException) {
                throw businessException;
            } else {
                throw new BusinessException(ErrorCode.API_INVOKE_ERROR, e, code);
            }
        }
    }

    /**
     * 请求api具体实现
     */
    @SuppressWarnings("DuplicatedCode")
    private HttpResult invokeImpl(HttpApiDetail apiDetail, Map<String, Object> params) {
        // 校验参数
        Map<String, String> paramErrors = new HashMap<>();
        for (ApiParamDto apiParamDto : apiDetail.getParamList()) {
            apiParamDto.validate(params.get(apiParamDto.getName()))
                    .ifPresent(error -> paramErrors.put(apiParamDto.getName(), error));
        }
        if (paramErrors.size() > 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, paramErrors);
        }
        // 获取sdk client
        HttpSdkClient sdkClient = sdkClientMap.get(apiDetail.getSdkCode());
        if (sdkClient == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "sdk(code=" + apiDetail.getSdkCode() + ")");
        }
        // 调用sdk client
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
