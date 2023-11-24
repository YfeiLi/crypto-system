package com.jy.crypto.system.api.service.client.ws;

import com.jy.crypto.system.api.dto.WsApiDetail;
import com.jy.crypto.system.api.dto.WsSdkDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Consumer;

@Component
@Scope("prototype")
public class WsSdkClient {

    private WsClient wsClient;
    private WsSdkDetail sdkDetail;

    public WsSdkClient(WsSdkDetail sdkDetail) {
        this.sdkDetail = sdkDetail;
    }

    @Autowired
    public void setWsClient(WsClient wsClient) {
        this.wsClient = wsClient;
    }

    public String subscribe(WsApiDetail apiDetail, Map<String, Object> params, Consumer<Object> listener) {
        // 参数校验
        return null;
    }

    public void unsubscribe(String listenerId) {

    }
}
