package com.usforever.wechatservice.service;

import com.google.common.cache.Cache;

/**
 * @Author ：yaxuSong
 * @Description: 愿意消耗一些内存空间来提升速度
 * @Date: 15:05 2018/12/18
 * @Modified by:
 */
public interface GuavaCacheService<K,V> extends Cache<K,V> {

}
