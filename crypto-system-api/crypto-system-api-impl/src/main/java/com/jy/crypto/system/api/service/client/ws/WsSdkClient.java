package com.jy.crypto.system.api.service.client.ws;

import com.jy.crypto.system.api.dto.ApiParamDto;
import com.jy.crypto.system.api.dto.WsApiDetail;
import com.jy.crypto.system.api.dto.WsSdkDetail;
import com.jy.crypto.system.infrastructure.exception.BusinessException;
import com.jy.crypto.system.infrastructure.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Component
@Scope("prototype")
public class WsSdkClient {

    private WsClient wsClient;
    private WsSdkDetail sdkDetail;

    public WsSdkClient(WsSdkDetail sdkDetail) {
        this.sdkDetail = sdkDetail;
        WebSocketClient wsClient = new StandardWebSocketClient();
        //WebSocketConnectionManager connectionManager = new WebSocketConnectionManager(client, new)
    }

    @Autowired
    public void setWsClient(WsClient wsClient) {
        this.wsClient = wsClient;
    }

    public String subscribe(WsApiDetail apiDetail, Map<String, Object> params, Consumer<Object> listener) {
        // 校验参数
        Map<String, String> paramErrors = new HashMap<>();
        for (ApiParamDto apiParamDto : apiDetail.getParamList()) {
            apiParamDto.validate(params.get(apiParamDto.getName()))
                    .ifPresent(error -> paramErrors.put(apiParamDto.getName(), error));
        }
        if (paramErrors.size() > 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, paramErrors);
        }
        return null;
    }

    public void unsubscribe(String listenerId) {

    }
}
