package com.tmser.core.listener;

import com.tmser.core.bo.LogObject;

/**
 * 日志记录接口
 * @author tjx
 * 2013-12-30
 * @version 2.0
 */
public interface LogListener {

	/**
	 * 记录日志
	 * 
	 * @param loginfo 操作描述信息
	 */
	void addLog(String loginfo);
	
	/**
	 * 记录日志
	 * 
	 * @param logObject 日志信息
	 */
	void addLog(LogObject logObject);
	
}
