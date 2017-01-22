/**
 * Mainbo.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.tmser.common.cache;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.util.StringUtils;


/**
 * <pre>
 *  基础缓存切面
 * </pre>
 *
 * @author tmser
 * @version $Id: BaseCacheAspect.java, v 1.0 2015年2月7日 下午5:08:54 tmser Exp $
 */
public abstract class BaseCacheAspect {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    private static CacheManager noOpCacheManager = new NoOpCacheManager();

    @Resource(name="cacheManger")
    private CacheManager cacheManager;
    
   public BaseCacheAspect(){
    	
    }
   
	protected Cache getCache(String cacheName) {
		Cache c = this.cacheManager.getCache(cacheName);
		if(c == null ){
			logger.warn("cache doesn't be found [{}],use blank cahe instead!",cacheName);
			c = noOpCacheManager.getCache(cacheName);
		}
		return c ;
	}

    /**
     * 缓存管理器
     *
     * @return
     */
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
    
    /**
     * 获取缓存管理器
     *
     * @return
     */
    protected CacheManager  getCacheManager() {
       return this.cacheManager;
    }

    public void clear(String cacheName) {
    	logger.debug("cacheName:{}, cache clear", cacheName);
        getCache(cacheName).clear();
    }

    public void evict(String cacheName,String key) {
    	logger.debug("cacheName:{}, evict key:{}",cacheName, key);
        getCache(cacheName).evict(key);
    }

    @SuppressWarnings("unchecked")
	public <T> T get(String cacheName,Object key) {
    	logger.debug("cacheName:{}, get key:{}", cacheName, key);
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        org.springframework.cache.Cache.ValueWrapper value = getCache(cacheName).get(key);
        if (value == null) {
            return null;
        }
        return (T) value.get();
    }

    public void put(String cacheName,String key, Object value) {
    	logger.debug("cacheName:{}, put key:{}",cacheName, key);
        getCache(cacheName).put(key, value);
    }

}
