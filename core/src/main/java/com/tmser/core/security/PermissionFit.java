package com.tmser.core.security;

import java.util.List;

/**
 * 权限获取器
 * @author tjx
 * @version 2.0
 * 2014-3-3
 */
public interface PermissionFit {

	/**
	 * 根据用户名获取用户权限集合
	 * @param username
	 * @return
	 */
	List<String> getPermissions(String username);
}
