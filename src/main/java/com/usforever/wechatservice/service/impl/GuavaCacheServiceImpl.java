package com.usforever.wechatservice.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheStats;
import com.google.common.collect.ImmutableMap;
import com.usforever.wechatservice.service.GuavaCacheService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @Author ：yaxuSong
 * @Description:
 * @Date: 15:30 2018/12/18
 * @Modified by:
 */
@Service("sessionCache")
public class GuavaCacheServiceImpl<K,V> implements GuavaCacheService<K,V> {

    private Cache<K,V> cache;

    /**
     * 最大缓存数
     */
    private Integer maxSize = 1000;

    /**
     * 微信过期时间（默认值）120分钟
     */
    private Integer expireTime = 119;

    /**
     * 数据存在时长，子类在构造方法中调用setExpireAfterWriteDuration(int duration)来更改
     */
    private TimeUnit MINUTE = TimeUnit.MINUTES;

    @PostConstruct
    public void init(){
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireTime,MINUTE)
                .build();
    }

    public GuavaCacheServiceImpl(){

    }

    public GuavaCacheServiceImpl(Integer expireTime){
        this.expireTime = expireTime;
    }

    @Override
    public V getIfPresent(Object o) {
        return cache.getIfPresent(o);
    }

    @Override
    public V get(K k, Callable<? extends V> callable) throws ExecutionException {
        return cache.get(k,callable);
    }

    @Override
    public ImmutableMap<K, V> getAllPresent(Iterable<?> iterable) {
        return cache.getAllPresent(iterable);
    }

    @Override
    public void put(K k, V v) {
        cache.put(k,v);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        cache.putAll(map);
    }

    @Override
    public void invalidate(Object o) {
        cache.invalidate(o);
    }

    @Override
    public void invalidateAll(Iterable<?> iterable) {
        cache.invalidateAll(iterable);
    }

    @Override
    public void invalidateAll() {
        cache.invalidateAll();
    }

    @Override
    public long size() {
        return cache.size();
    }

    @Override
    public CacheStats stats() {
        return cache.stats();
    }

    @Override
    public ConcurrentMap<K, V> asMap() {
        return cache.asMap();
    }

    @Override
    public void cleanUp() {
        cache.cleanUp();
    }
}
