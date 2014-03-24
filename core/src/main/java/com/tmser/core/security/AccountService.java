package com.tmser.core.security;

/**
 * 账户服务接口
 * @author tjx
 * @version 2.0
 * 2014-3-3
 */
public interface AccountService {

	/**
	 * 根据账户名查询账户信息
	 * @param username
	 * @return
	 */
	Account getAcount(String username);
}
