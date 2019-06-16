package com.zeroq6.common.cache;

import com.google.common.cache.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class GuavaCacheService implements CacheServiceApi {


    private final int maxSize = 100000;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String nullValue = GuavaCacheService.class.getCanonicalName() + "_null_value_with_key";

    // 最大支持30天
    // 不支持每次put设置过期时间
    private final LoadingCache<String, String> guavaCache =
            CacheBuilder.newBuilder()
                    .maximumSize(maxSize)
                    .expireAfterAccess(DEFAULT_EXPIRED_IN_SECONDS, TimeUnit.SECONDS)
                    .removalListener(new RemovalListener<String, String>() {
                        @Override
                        public void onRemoval(RemovalNotification<String, String> removalNotification) {
                            logger.info("缓存被移除：key={}，value={}，cause={}", removalNotification.getKey(), removalNotification.getValue(), removalNotification.getCause());
                        }
                    })
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
