package com.dhyjlas.mys.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

/**
 * 本地缓存
 */
public enum LocalCache {
    /**
     * 1
     */
    TICKET(100, 50000, 1, TimeUnit.DAYS),
    STOKEN(100, 50000, 1, TimeUnit.DAYS),
    UID(100, 50000, 1, TimeUnit.DAYS),
    TASK(100, 50000, 1, TimeUnit.DAYS),
    BOOK_DOWNLOAD(100, 50000, 1, TimeUnit.DAYS)

    ;

    private final Cache<Object, Object> cache;

    LocalCache() {
        cache = Caffeine.newBuilder().build();
    }

    /**
     * @param duration        过期时间
     * @param unit            时间单位
     */
    LocalCache(long duration, TimeUnit unit) {
        cache = Caffeine.newBuilder()
                .expireAfterAccess(duration, unit)
                .build();
    }

    /**
     * @param initialCapacity 初始缓存大小
     * @param maximumSize     缓存上限
     * @param duration        过期时间
     * @param unit            时间单位
     */
    LocalCache(int initialCapacity, long maximumSize, long duration, TimeUnit unit) {
        cache = Caffeine.newBuilder()
                .initialCapacity(initialCapacity)
                .maximumSize(maximumSize)
                .expireAfterAccess(duration, unit)
                .build();
    }

    /**
     * 获取缓存对象，用户调用Cache原有方法
     * @return Cache<Object, Object>
     */
    public Cache<Object, Object> cache() {
        return cache;
    }

    /**
     * 查询本地缓存
     * @param o
     * @return Object
     */
    public Object getIfPresent(Object o) {
        return cache.getIfPresent(o);
    }

    /**
     * 写入本地缓存
     * @param k
     * @param v
     */
    public void put(Object k, Object v) {
        cache.put(k, v);
    }

    /**
     * 删除指定的缓存
     * @param o
     */
    public void invalidate(Object o){
        cache.invalidate(o);
    }
}
