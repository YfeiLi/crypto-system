package com.jy.crypto.system.api.service;

import com.jy.crypto.system.api.facade.dto.HttpResult;
import com.jy.crypto.system.api.service.client.http.HttpApiClient;
import com.jy.crypto.system.api.service.client.ws.WsApiClient;
import com.jy.crypto.system.infrastructure.utils.JacksonHelper;
import lombok.RequiredArgsConstructor;
import okhttp3.Request;
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
    public String subscribe(String apiCode, Map<String, Object> params, Consumer<Object> listener) {
        return wsApiClient.subscribe(apiCode, params, listener);
    }

    /**
     * websocket取消订阅
     * @param listenerId subscribe方法返回值
     */
    public void unsubscribe(String apiCode, String listenerId) {
        wsApiClient.unsubscribe(apiCode, listenerId);
    }
}
