package com.tmser.common.orm;


/**
 * 映射配置获取
 * @author tjx
 * @version 2.0
 * 2014-1-17
 */
public interface MapperAware {

	/**
	 * 获取表映射类
	 * @return
	 */
	Table getTable();
	
}
