package com.tmser.core.cache;

/**
 *
 * @author tjx
 * @version 2.0
 * 2014-3-29
 */
public interface Cache {

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
