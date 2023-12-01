package com.jy.crypto.system.api.service.client.http;

import com.jy.crypto.system.account.facade.AccountFacade;
import com.jy.crypto.system.account.facade.dto.AccountDto;
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

import java.util.Arrays;
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
    private final AccountFacade accountFacade;
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
    public HttpResult invoke(String code, Long accountId, Map<String, Object> params) {
        // 获取api详情
        HttpApiDetail apiDetail = apiReadService.getHttpApiDetail(code);
        // 忽略不参与缓存的参数
        Map<String, Object> paramsForCache = new HashMap<>(params);
        if (apiDetail.getIgnoreCacheHitParams() != null) {
            paramsForCache.entrySet().removeIf(entry ->
                    Arrays.stream(apiDetail.getIgnoreCacheHitParams()).anyMatch(key -> key.equals(entry.getKey())));
        }
        Future<HttpResult> future = null;
        // 检查缓存
        if (apiDetail.isCache()) {
            future = httpCache.get(code, accountId, paramsForCache);
        }
        if (future == null) {
            // 调用具体实现
            future = CompletableFuture.supplyAsync(() -> invokeImpl(apiDetail, accountId, params));
            // 设置缓存
            if (apiDetail.isCache()) {
                httpCache.set(code, accountId, paramsForCache, future, apiDetail.getCacheMills());
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
    private HttpResult invokeImpl(HttpApiDetail apiDetail, Long accountId, Map<String, Object> params) {
        String code = apiDetail.getCode();
        // 校验参数
        Map<String, String> paramErrors = new HashMap<>();
        for (ApiParamDto apiParamDto : apiDetail.getParamList()) {
            apiParamDto.validate(params.get(apiParamDto.getName()))
                    .ifPresent(error -> paramErrors.put(apiParamDto.getName(), error));
        }
        if (paramErrors.size() > 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, paramErrors);
        }
        // 获取账户详情
        AccountDto account = null;
        if (accountId != null) {
            account = accountFacade.getById(accountId);
            if (!account.getExchange().equals(apiDetail.getExchange())) {
                throw new BusinessException(ErrorCode.DATA_INCONSISTENT,
                        "exchange of account(id=" + accountId + ")",
                        "exchange of api(code=" + code + ")");
            }
        } else if(!apiDetail.getIsGlobal()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "accountId required");
        }
        // 获取sdk客户端
        HttpSdkClient sdkClient = sdkClientMap.get(apiDetail.getSdkCode());
        if (sdkClient == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "sdk(code=" + apiDetail.getSdkCode() + ")");
        }
        // 调用sdk client
        return sdkClient.invoke(apiDetail, params, account);
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
