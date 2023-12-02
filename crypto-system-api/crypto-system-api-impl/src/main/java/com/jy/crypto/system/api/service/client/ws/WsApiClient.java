package com.jy.crypto.system.api.service.client.ws;

import com.jy.crypto.system.account.facade.AccountFacade;
import com.jy.crypto.system.account.facade.dto.AccountDto;
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
    private final AccountFacade accountFacade;

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
    public String subscribe(String code, Long accountId, Map<String, Object> params, Consumer<Object> listener) {
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
        // 获取sdk client
        WsSdkClient sdkClient = sdkClientMap.get(apiDetail.getSdkCode());
        if (sdkClient == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "sdk(code=" + apiDetail.getSdkCode() + ")");
        }
        // 调用sdk client
        return sdkClient.subscribe(apiDetail, params, account, listener);
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
