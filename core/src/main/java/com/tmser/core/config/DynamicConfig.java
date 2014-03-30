package com.tmser.core.config;


/**
 * 动态配置获取工具类
 * @author tjx
 * @version 2.0
 * 2014-3-29
 */
public class DynamicConfig {
	
	/**
	 * 根据key 值获取配置
	 * @param key
	 * @return
	 */
	public static String getConfig(String key){
		return getConfig(key,null);
	}
	
	/**
	 * 根据动态
	 * @param key
	 * @param def
	 * @return
	 */
	public static String getConfig(String key,String def){
		return key;
	}
}
