package com.jy.crypto.system.api.facade;

import com.jy.crypto.system.api.facade.dto.HttpResult;
import com.jy.crypto.system.api.service.ApiInvokeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Component
public class ApiFacadeImpl implements ApiFacade {

    private final ApiInvokeService apiInvokeService;

    @Override
    public HttpResult invokeHttp(String apiCode, Long accountId, Map<String, Object> params) {
        return apiInvokeService.invokeHttp(apiCode, accountId, params);
    }

    @Override
    public String subscribe(String apiCode, Long accountId, Map<String, Object> params, Consumer<Object> listener) {
        return apiInvokeService.subscribe(apiCode, accountId, params, listener);
    }

    @Override
    public void unsubscribe(String apiCode, String listenerId) {
        apiInvokeService.unsubscribe(apiCode, listenerId);
    }
}
