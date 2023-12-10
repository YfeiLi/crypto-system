package com.jy.crypto.system.api.service.client.ws;

import com.jy.crypto.system.api.dto.WsApiDetail;
import com.jy.crypto.system.api.dto.WsSdkDetail;
import com.jy.crypto.system.api.service.ApiReadService;
import com.jy.crypto.system.infrastructure.exception.BusinessException;
import com.jy.crypto.system.infrastructure.exception.ErrorCode;
import com.jy.crypto.system.script.facade.ScriptFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Component
@Scope("prototype")
public class WsSdkClient {

    private final WsSdkDetail sdkDetail;
    private ApiReadService apiReadService;
    private ScriptFacade scriptFacade;
    private final Map<Integer, WsConnect> connectMap = new ConcurrentHashMap<>();
    private final Map<SubscribeRecord, List<String>> subscribeMap = new ConcurrentHashMap<>();

    public WsSdkClient(WsSdkDetail sdkDetail) {
        this.sdkDetail = sdkDetail;
    }

    @Autowired
    public void setApiReadService(ApiReadService apiReadService) {
        this.apiReadService = apiReadService;
    }

    @Autowired
    public void setScriptFacade(ScriptFacade scriptFacade) {
        this.scriptFacade = scriptFacade;
    }

    /**
     * 订阅
     */
    public String subscribe(WsApiDetail apiDetail, Map<String, Object> params, Consumer<String> consumer) {
        // 生成订阅id
        String subscribeId = UUID.randomUUID().toString().replace("-", "");
        // 获取连接
        WsConnect connect = getConnect(apiDetail, params);
        // 为连接添加消费者
        connect.addConsumer(msg -> handleReceivedMsg(apiDetail, params, msg, consumer), subscribeId);
        // 如果有订阅消息脚本，则发送订阅消息
        if (sdkDetail.getSubscribeMsgGenerateScriptId() != null) {
            sendSubscribeMsg(apiDetail, params, subscribeId, connect);
        }
        return subscribeId;
    }

    /**
     * 取消订阅
     */
    public void unsubscribe(String subscribeId) {
        // 移除消费者，并且找到连接
        WsConnect connect = connectMap.values().stream()
                .filter(item -> item.removeConsumer(subscribeId))
                .findFirst().orElse(null);
        if (connect == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "connect(subscribeId=" + subscribeId + ")");
        }
        if (connect.hasConsumer()) {
            // 如果还有消费者，并且有取消订阅消息脚本，则发送取消订阅消息
            if (sdkDetail.getUnsubscribeMsgGenerateScriptId() != null) {
                sendUnsubscribeMsg(subscribeId, connect);
            }
        } else {
            // 如果连接已无消费者，则断开并移除连接
            connect.disconnect();
            connectMap.entrySet().removeIf(entry -> entry.getValue() == connect);
        }
    }

    /**
     * 获取连接
     */
    private WsConnect getConnect(WsApiDetail apiDetail, Map<String, Object> params) {
        // 调用脚本获取连接哈希值
        Map<String, Object> connectHashCodeVariables = Map.of(
                "api", apiDetail, "sdk", sdkDetail,
                "params", params);
        Object connectHashCodeScriptResult = scriptFacade
                .execute(sdkDetail.getConnectHashCodeScriptId(), connectHashCodeVariables);
        if (!(connectHashCodeScriptResult instanceof Integer connectHashCode)) {
            throw new BusinessException(ErrorCode.SCRIPT_RESULT_TYPE_ERROR, sdkDetail.getConnectHashCodeScriptId());
        }
        return connectMap.computeIfAbsent(connectHashCode, key -> {
            // 调用脚本生成url
            Map<String, Object> subscribeHandlerVariables = Map.of(
                    "api", apiDetail, "sdk", sdkDetail,
                    "params", params);
            Object urlGenerateScriptResult = scriptFacade
                    .execute(sdkDetail.getUrlGenerateScriptId(), subscribeHandlerVariables);
            if (!(urlGenerateScriptResult instanceof String url)) {
                throw new BusinessException(ErrorCode.SCRIPT_RESULT_TYPE_ERROR, sdkDetail.getUrlGenerateScriptId());
            }
            try {
                return new WsConnect(url);
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.API_INVOKE_ERROR, e, apiDetail.getCode());
            }
        });
    }

    /**
     * 发送订阅消息
     */
    private void sendSubscribeMsg(WsApiDetail apiDetail, Map<String, Object> params,
                                  String subscribeId, WsConnect connect) {
        SubscribeRecord subscribeRecord = new SubscribeRecord(apiDetail.getCode(), params);
        // 判断是否发送过订阅消息
        if (subscribeMap.get(subscribeRecord) == null) {
            // 调用脚本获取订阅消息
            Map<String, Object> subscribeMsgGenerateVariables = Map.of(
                    "api", apiDetail, "sdk", sdkDetail,
                    "params", params);
            Object subscribeMsgGenerateScriptResult = scriptFacade
                    .execute(sdkDetail.getSubscribeMsgGenerateScriptId(), subscribeMsgGenerateVariables);
            if (!(subscribeMsgGenerateScriptResult instanceof String subscribeMsg)) {
                throw new BusinessException(ErrorCode.SCRIPT_RESULT_TYPE_ERROR,
                        sdkDetail.getSubscribeMsgGenerateScriptId());
            }
            // 发送订阅消息
            try {
                connect.send(subscribeMsg);
            } catch (IOException e) {
                throw new BusinessException(ErrorCode.API_INVOKE_ERROR, e, apiDetail.getCode());
            }
        }
        subscribeMap.computeIfAbsent(subscribeRecord, code -> new ArrayList<>()).add(subscribeId);
    }

    /**
     * 发送取消订阅消息
     */
    private void sendUnsubscribeMsg(String subscribeId, WsConnect connect) {
        // 获取订阅记录，并移除订阅id
        SubscribeRecord subscribeRecord = subscribeMap.entrySet().stream()
                .filter(item -> item.getValue().remove(subscribeId))
                .map(Map.Entry::getKey)
                .findFirst().orElse(null);
        if (subscribeRecord == null) {
            return;
        }
        // 判断是否还有消费者应用该订阅
        if (subscribeMap.get(subscribeRecord).isEmpty()) {
            // 获取apiDetail
            WsApiDetail apiDetail = apiReadService.getWsApiDetail(subscribeRecord.apiCode());
            // 调用脚本获取取消订阅消息
            Map<String, Object> unsubscribeMsgGenerateVariables = Map.of(
                    "api", apiDetail, "sdk", sdkDetail,
                    "params", subscribeRecord.params());
            Object unsubscribeMsgGenerateScriptResult = scriptFacade
                    .execute(sdkDetail.getUnsubscribeMsgGenerateScriptId(), unsubscribeMsgGenerateVariables);
            if (!(unsubscribeMsgGenerateScriptResult instanceof String unsubscribeMsg)) {
                throw new BusinessException(ErrorCode.SCRIPT_RESULT_TYPE_ERROR,
                        sdkDetail.getUnsubscribeMsgGenerateScriptId());
            }
            // 发送消息
            try {
                connect.send(unsubscribeMsg);
            } catch (IOException e) {
                throw new BusinessException(ErrorCode.API_INVOKE_ERROR, e, apiDetail.getCode());
            }
            // 移除空记录
            subscribeMap.remove(subscribeRecord);
        }
    }

    /**
     * 处理接收的消息
     */
    private void handleReceivedMsg(WsApiDetail apiDetail, Map<String, Object> params,
                                   String msg, Consumer<String> consumer) {
        if (sdkDetail.getMsgReceiveFilterScriptId() != null) {
            // 调用脚本获取订阅消息
            Map<String, Object> msgReceiveFilterVariables = Map.of(
                    "api", apiDetail, "sdk", sdkDetail,
                    "params", params, "message", msg);
            Object publishRouterScriptResult = scriptFacade
                    .execute(sdkDetail.getMsgReceiveFilterScriptId(), msgReceiveFilterVariables);
            if (!(publishRouterScriptResult instanceof Boolean publishRouterResult)) {
                throw new BusinessException(ErrorCode.SCRIPT_RESULT_TYPE_ERROR,
                        sdkDetail.getSubscribeMsgGenerateScriptId());
            }
            if (!publishRouterResult) {
                return;
            }
        }
        consumer.accept(msg);
    }

    private record SubscribeRecord(String apiCode, Map<String, Object> params) {}
}
