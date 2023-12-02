package com.jy.crypto.system.api.service.client.ws;

import com.jy.crypto.system.account.facade.dto.AccountDto;
import com.jy.crypto.system.api.dto.WsApiDetail;
import com.jy.crypto.system.api.dto.WsSdkDetail;
import com.jy.crypto.system.script.facade.ScriptFacade;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Component
@Scope("prototype")
public class WsSdkClient {

    private WsClient wsClient;
    private WsSdkDetail sdkDetail;
    private ScriptFacade scriptFacade;
    private Map<String, SubscribeItem> subscribeMap = new HashMap<>();

    public WsSdkClient(WsSdkDetail sdkDetail) {
        this.sdkDetail = sdkDetail;
        WebSocketClient wsClient = new StandardWebSocketClient();
        //WebSocketConnectionManager connectionManager = new WebSocketConnectionManager(client, new)
    }

    @Autowired
    public void setWsClient(WsClient wsClient) {
        this.wsClient = wsClient;
    }

    public String subscribe(WsApiDetail apiDetail, Map<String, Object> params, AccountDto account, Consumer<Object> listener) {
        // 调用脚本获取订阅
        Map<String, Object> subscribeHandlerVariables = new HashMap<>();
        subscribeHandlerVariables.put("api", apiDetail);
        subscribeHandlerVariables.put("sdk", sdkDetail);
        subscribeHandlerVariables.put("account", account);
        subscribeHandlerVariables.put("params", params);
        Object subscribeHandlerResult = scriptFacade.execute(sdkDetail.getSubscribeHandlerScriptId(), subscribeHandlerVariables);

        return null;
    }

    public void unsubscribe(String listenerId) {
        subscribeMap.remove(listenerId);
    }

    @Data
    private static class SubscribeItem {
        private Consumer<Object> listener;
        private Map<String, Object> params;
    }
}
