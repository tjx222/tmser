package com.tmser.common.orm;

/**
 * sql 解析接口
 * @author jxtan
 * @date 2014年10月24日
 */
public interface SqlMapping {
	
	public static final String LIKE_PRFIX = "__#%";

	/**
	 * 根据映射配置，解析
	 * @param sql
	 * @return
	 */
	 String mapping(String sql);
}
