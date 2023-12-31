package com.jy.crypto.system.api.service;

import com.jy.crypto.system.api.facade.dto.HttpResult;
import com.jy.crypto.system.api.service.client.http.HttpApiClient;
import com.jy.crypto.system.api.service.client.ws.WsApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Api调用服务
 */
@RequiredArgsConstructor
@Service
public class ApiInvokeService {

    private final HttpApiClient httpApiClient;
    private final WsApiClient wsApiClient;

    /**
     * 调用http api
     */
    public HttpResult invokeHttp(String apiCode, Map<String, Object> params) {
        return httpApiClient.invoke(apiCode, params);
    }

    /**
     * websocket订阅
     */
    public String subscribe(String apiCode, Map<String, Object> params, Consumer<String> consumer) {
        return wsApiClient.subscribe(apiCode, params, consumer);
    }

    /**
     * websocket取消订阅
     * @param subscribeId subscribe方法返回值
     */
    public void unsubscribe(String apiCode, String subscribeId) {
        wsApiClient.unsubscribe(apiCode, subscribeId);
    }
}
