package com.zeroq6.common.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.TimeUnit;

public class GuavaCacheService implements CacheServiceApi {


    private final int maxSize = 100000;

    private final String nullValue = GuavaCacheService.class.getCanonicalName() + "_null_value_with_key";

    // 最大支持30天
    // 不支持每次put设置过期时间
    private final LoadingCache<String, String> guavaCache =
            CacheBuilder.newBuilder()
                    .maximumSize(maxSize) // 最大在线数的5倍 + 10
                    .expireAfterAccess(DEFAULT_EXPIRED_IN_SECONDS, TimeUnit.SECONDS)
                    .build(new CacheLoader<String, String>() {
                        @Override
                        public String load(String key) {
                            return nullValue; // 避免 CacheLoader returned null for key xxx异常
                        }
                    });

    @Override
    public String get(String key) throws Exception {
        String v = guavaCache.get(key);
        return nullValue.equals(v) ? null : v;
    }

    @Override
    public boolean set(String key, String value) throws Exception {
        return set(key, value, DEFAULT_EXPIRED_IN_SECONDS);
    }

    @Override
    public boolean set(String key, String value, int expiredInSeconds) throws Exception {
        guavaCache.put(key, value);
        return true;
    }

    @Override
    public boolean remove(String key) throws Exception {
        guavaCache.invalidate(key);
        return true;
    }


}
