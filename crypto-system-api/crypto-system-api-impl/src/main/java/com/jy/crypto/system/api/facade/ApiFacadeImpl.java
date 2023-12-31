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
    public HttpResult invokeHttp(String apiCode, Map<String, Object> params) {
        return apiInvokeService.invokeHttp(apiCode, params);
    }

    @Override
    public String subscribe(String apiCode, Map<String, Object> params, Consumer<String> consumer) {
        return apiInvokeService.subscribe(apiCode, params, consumer);
    }

    @Override
    public void unsubscribe(String apiCode, String subscribeId) {
        apiInvokeService.unsubscribe(apiCode, subscribeId);
    }
}
