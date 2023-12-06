package com.jy.crypto.system.api.facade;

import com.jy.crypto.system.api.facade.dto.HttpResult;

import java.util.Map;
import java.util.function.Consumer;

public interface ApiFacade {

    /**
     * 调用http api
     */
    HttpResult invokeHttp(String apiCode, Long accountId, Map<String, Object> params);

    /**
     * 订阅 websocket
     */
    String subscribe(String apiCode, Long accountId, Map<String, Object> params, Consumer<String> consumer);

    /**
     * 取消订阅 websocket
     * @param listenerId subscribe方法返回值
     */
    void unsubscribe(String apiCode, String listenerId);
}
