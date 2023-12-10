package com.jy.crypto.system.api.service.client.http;

import com.jy.crypto.system.api.facade.dto.HttpResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

@Component
public class HttpCache {

    private final Map<CacheKey, CacheEntry> cacheMap = new ConcurrentHashMap<>();

    @Scheduled(cron = "0 0/5 * * * ?")
    public void deleteExpiredCache() {
        cacheMap.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    public Future<HttpResult> get(String apiCode, Map<String, Object> params) {
        CacheKey key = new CacheKey(apiCode, params);
        CacheEntry entry = cacheMap.get(key);
        if (entry != null) {
            if (entry.isExpired()) {
                cacheMap.remove(key);
            } else {
                return entry.future();
            }
        }
        return null;
    }

    public void set(String apiCode, Map<String, Object> params, Future<HttpResult> future, Long cacheMills) {
        CacheKey key = new CacheKey(apiCode, params);
        CacheEntry entry = new CacheEntry(future, System.currentTimeMillis() + cacheMills);
        cacheMap.put(key, entry);
    }

    private record CacheEntry(Future<HttpResult> future, long expiryTime) {
        public Boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    private record CacheKey(String apiCode, Map<String, Object> params) {}
}
