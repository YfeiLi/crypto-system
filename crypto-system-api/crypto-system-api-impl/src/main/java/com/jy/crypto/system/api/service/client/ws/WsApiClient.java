package com.jy.crypto.system.api.service.client.ws;

import com.jy.crypto.system.api.dto.WsApiDetail;
import com.jy.crypto.system.api.dto.WsSdkDetail;
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
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
@Component
public class WsApiClient {

    private final Map<String, WsSdkClient> sdkClientMap = new ConcurrentHashMap<>();

    private final ApplicationContext applicationContext;
    private final ApiReadService apiReadService;
    private final ApiSdkReadService apiSdkReadService;

    /**
     * 初始化sdk
     */
    @PostConstruct
    public void init() {
        List<WsSdkDetail> wsSdkDetailList = apiSdkReadService.getAllWsSdkDetail();
        for (WsSdkDetail wsSdkDetail : wsSdkDetailList) {
            loadSdk(wsSdkDetail);
        }
    }

    /**
     * 订阅
     */
    public String subscribe(String code, Map<String, Object> params, Consumer<Object> listener) {
        WsApiDetail apiDetail = apiReadService.getWsApiDetail(code);
        WsSdkClient sdkClient = sdkClientMap.get(apiDetail.getSdkCode());
        if (sdkClient == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "sdk(code=" + apiDetail.getSdkCode() + ")");
        }
        return sdkClient.subscribe(apiDetail, params, listener);
    }

    /**
     * 取消订阅
     * @param listenerId subscribe方法返回值
     */
    public void unsubscribe(String code, String listenerId) {
        WsApiDetail apiDetail = apiReadService.getWsApiDetail(code);
        WsSdkClient sdkClient = sdkClientMap.get(apiDetail.getSdkCode());
        if (sdkClient == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "sdk(code=" + apiDetail.getSdkCode() + ")");
        }
        sdkClient.unsubscribe(listenerId);
    }

    /**
     * 加载sdk
     */
    public void loadSdk(WsSdkDetail sdkDetail) {
        WsSdkClient sdkClient = applicationContext.getBean(WsSdkClient.class, sdkDetail);
        sdkClientMap.put(sdkDetail.getCode(), sdkClient);
        log.info("load sdk({}) success", sdkDetail.getCode());
    }

    /**
     * 移除sdk
     */
    public void removeSdk(String sdkCode) {
        sdkClientMap.remove(sdkCode);
    }
}
