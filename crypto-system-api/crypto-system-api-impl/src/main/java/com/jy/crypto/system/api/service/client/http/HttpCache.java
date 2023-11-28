package com.jy.crypto.system.api.service.client.http;

import com.jy.crypto.system.api.facade.dto.HttpResult;

import java.time.OffsetDateTime;
import java.util.*;

public class HttpCache {

    private static final List<CacheItem> caches = new ArrayList<>();

    public static HttpResult get(String code, Long accountId, Map<String, Object> params) {
        synchronized (caches) {
            for (CacheItem cache : caches) {
                Map<String, Object> copyParams = null;
                if (params != null) {
                    copyParams = new HashMap<>(params);
                    for (String ignoreCacheHitParam : cache.ignoreCacheHitParas()) {
                        copyParams.remove(ignoreCacheHitParam);
                    }
                }
                if (Objects.equals(code, cache.code())
                        && Objects.equals(accountId, cache.accountId())
                        && Objects.equals(copyParams, cache.params())) {
                    if (cache.time().plusNanos(cache.cacheMills() * 1000000).isAfter(OffsetDateTime.now())) {
                        return cache.httpResult();
                    } else {
                        caches.remove(cache);
                    }
                }
            }
            return null;
        }
    }

    public static void set(String code, Long accountId, Map<String, Object> params,
                           Long cacheMills, String[] ignoreCacheHitParams, HttpResult httpResult) {
        synchronized(caches) {
            Map<String, Object> copyParams = new HashMap<>(params);
            for (String ignoreCacheHitParam : ignoreCacheHitParams) {
                copyParams.remove(ignoreCacheHitParam);
            }
            CacheItem cacheItem = new CacheItem(code, accountId, copyParams,
                    cacheMills, ignoreCacheHitParams, OffsetDateTime.now(), httpResult);
            caches.add(cacheItem);
        }
    }

    private record CacheItem(String code, Long accountId, Map<String, Object> params,
                             Long cacheMills, String[] ignoreCacheHitParas, OffsetDateTime time,
                             HttpResult httpResult) {}

}
