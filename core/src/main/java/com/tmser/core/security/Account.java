/**
 * 
 */
package com.tmser.core.security;

/**
 * 用户接口
 * @author tjx
 * @version 2.0
 * 2014-3-3
 */
public interface Account {
	/**
	 * 获取用户名
	 * @return
	 */
	String getUsername();
	
	/**
	 * 获取密码
	 * @return
	 */
	String getPassword();
}
