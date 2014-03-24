package com.tmser.core.utils;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tmser.core.service.AppCacheProvider;

/**
 * 缓存加载器
 * @author 张凯
 * @date 2014-2-11
 */
public class CacheLoader {

	private static final Logger log = LoggerFactory.getLogger(CacheLoader.class);
	
	/**
	 * 服务提供者集合
	 */
	private static final Map<String, AppCacheProvider> providers = new HashMap<String, AppCacheProvider>();

	/**
	 * 根据缓存类型获取对应Map，可根据开关是否取缓存
	 * @param cacheType 缓存类型
	 * @param isCache 是否取缓存
	 * @return
	 */
	public static Object getCache(String cacheType, boolean isCache) {
		AppCacheProvider provider = providers.get(cacheType);
		if(provider == null) {
			log.error("未找到[" + cacheType + "]，目前已有[" + providers.keySet() + "].");
		}
		return provider.getCache(cacheType, isCache);
	}

	/**
	 * 根据缓存类型和数据key，获取相应value，可根据开关是否取缓存
	 * @param cacheType 缓存类型
	 * @param id 需要翻译的ID
	 * @param isCache 是否取缓存
	 * @return
	 */
	public static Object getCache(String cacheType, String id, boolean isCache) {
		AppCacheProvider provider = providers.get(cacheType);
		if(provider == null) {
			log.error("未找到[" + cacheType + "]，目前已有[" + providers.keySet() + "].");
		}
		return provider.getCache(cacheType, id, isCache);
	}

	/**
	 * 注册服务提供者
	 * @param cacheType
	 * @param provider
	 */
	public static void registerProvider(String cacheType, AppCacheProvider provider) {
		AppCacheProvider old = providers.get(cacheType);
		if(old != null) {
			throw new RuntimeException("注册缓存服务提供者时发生异常，已经为[" + cacheType + "]注册了：" 
					+ old + "，现在又希望注册：" + provider);
		}else {
			log.info("注册缓存服务提供者[" + cacheType + "]" + "---" + provider);
		}
		
		providers.put(cacheType, provider);
	}
}
