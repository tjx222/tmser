package com.tmser.core.security;

import java.util.List;

public interface RoleFit {

	/**
	 * 获取用户所有角色
	 * @param username
	 * @return
	 */
	List<String> getRoles(String username);
	
	/**
	 * 根据用户名及角色获取权限
	 * @param username
	 * @param role
	 * @return
	 */
	List<String> getPermissions(String username, String role);
}
