package com.jy.crypto.system.api.service.client.ws;

import com.jy.crypto.system.infrastructure.exception.BusinessException;
import com.jy.crypto.system.infrastructure.exception.ErrorCode;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class WsConnect {
    private final String url;
    private final WebSocketConnectionManager connectManager;
    private final Map<String, Consumer<String>> consumerMap = new ConcurrentHashMap<>();
    private WebSocketSession session;
    private final static int CONNECT_TIMEOUT_MILLS = 5000;
    private final static int RECONNECT_INTERVAL_MILLS = 1000;

    public WsConnect(String url) throws Exception {
        this.url = url;
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        CompletableFuture<Void> establishFuture = new CompletableFuture<>();
        connectManager = new WebSocketConnectionManager(webSocketClient, new TextWebSocketHandler() {
            @Override
            public void afterConnectionEstablished(@NotNull WebSocketSession session) {
                WsConnect.this.session = session;
                if (!establishFuture.isDone()) {
                    establishFuture.complete(null);
                }
            }

            @Override
            protected void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) {
                for (Consumer<String> consumer : consumerMap.values()) {
                    consumer.accept(message.getPayload());
                }
            }

            @Override
            public void handleTransportError(@NotNull WebSocketSession session, @NotNull Throwable exception) {
                reconnect();
            }

            @Override
            public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) {
                reconnect();
            }
        }, url);
        connectManager.start();
        establishFuture.get(CONNECT_TIMEOUT_MILLS, TimeUnit.MILLISECONDS);
    }

    public void send(String msg) throws IOException {
        if (session != null) {
            session.sendMessage(new TextMessage(msg));
        } else {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "ws session(url=" + url + ")");
        }
    }

    public void addConsumer(Consumer<String> consumer, String consumerId) {
        consumerMap.put(consumerId, consumer);
    }

    public Boolean removeConsumer(String consumerId) {
        return consumerMap.remove(consumerId) != null;
    }

    public Boolean hasConsumer() {
        return !consumerMap.isEmpty();
    }

    public void disconnect() {
        connectManager.stop();
    }

    private void reconnect() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                connectManager.stop();
                connectManager.start();
            }
        }, RECONNECT_INTERVAL_MILLS);
    }
}
