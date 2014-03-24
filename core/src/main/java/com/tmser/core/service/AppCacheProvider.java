package com.tmser.core.service;

import org.springframework.beans.factory.InitializingBean;

/**
 * 应用缓存提供者
 * @author 张凯
 * @date 2014-1-18
 *
 */
public interface AppCacheProvider extends InitializingBean{

	/**
	 * 根据缓存类型获取缓存对象
	 * @param cacheType 缓存类型
	 * @param isCache 是否取缓存
	 * @return
	 */
	public Object getCache(String cacheType, boolean isCache);
	
	/**
	 * 根据缓存类型和缓存编码，获取缓存对象
	 * @param cacheType 缓存类型
	 * @param code 缓存编码
	 * @param isCache 是否取缓存
	 * @return
	 */
	public Object getCache(String cacheType, String code, boolean isCache);
}
