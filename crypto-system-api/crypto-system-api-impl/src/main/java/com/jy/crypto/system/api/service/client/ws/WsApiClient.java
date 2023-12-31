package com.jy.crypto.system.api.service.client.ws;

import com.jy.crypto.system.api.dto.ApiParamDto;
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

import java.util.HashMap;
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
    @SuppressWarnings("DuplicatedCode")
    public String subscribe(String code, Map<String, Object> params, Consumer<String> consumer) {
        WsApiDetail apiDetail = apiReadService.getWsApiDetail(code);
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
        WsSdkClient sdkClient = sdkClientMap.get(apiDetail.getSdkCode());
        if (sdkClient == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "sdk(code=" + apiDetail.getSdkCode() + ")");
        }
        // 调用sdk client
        return sdkClient.subscribe(apiDetail, params, consumer);
    }

    /**
     * 取消订阅
     * @param subscribeId subscribe方法返回值
     */
    public void unsubscribe(String code, String subscribeId) {
        WsApiDetail apiDetail = apiReadService.getWsApiDetail(code);
        WsSdkClient sdkClient = sdkClientMap.get(apiDetail.getSdkCode());
        if (sdkClient == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "sdk(code=" + apiDetail.getSdkCode() + ")");
        }
        sdkClient.unsubscribe(subscribeId);
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
