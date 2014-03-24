package com.tmser.core.security.shiro;

import java.util.ArrayList;
import java.util.Collection;


import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import com.tmser.core.security.Account;
import com.tmser.core.security.AccountService;
import com.tmser.core.security.PermissionFit;
import com.tmser.core.security.RoleFit;

/**
 * Shiro Realm 实现
 * @author tjx
 *
 */
public class TspJDBCRealm extends AuthorizingRealm{
	
	private AccountService accountService;
	/**
	 * 权限获取器
	 */
	private PermissionFit permissionFit;
	
	/**
	 * 角色及角色权限获取器
	 */
	private RoleFit roleFit;
	

	public void setPermissionFit(PermissionFit permissionFit) {
		this.permissionFit = permissionFit;
	}

	public void setRoleFit(RoleFit roleFit) {
		this.roleFit = roleFit;
	}

	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}

	/**
	 * 授权信息
	 */
	protected AuthorizationInfo doGetAuthorizationInfo(
			PrincipalCollection principals) {
		
		String username = (String) principals.fromRealm(getName()).iterator().next();
		
		if( username != null ){
			Collection<String> pers = new ArrayList<String>();
			SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
			if( roleFit != null){
				Collection<String> roles = roleFit.getRoles(username);
				for( String role : roles ){
					if( role != null)
						info.addRole(role);
					Collection<String> ps = roleFit.getPermissions(username,role);
					if( ps != null )
						pers.addAll( pers );
				}
				
				if(permissionFit != null){//单个权限
					Collection<String> tps = permissionFit.getPermissions(username);
					if(tps != null) 
						pers.addAll(tps);
				}
				
				info.addStringPermissions(pers);
				return info;
			}
		}
		
		return null;
	}

	/**
	 * 认证信息
	 */
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken authcToken ) throws AuthenticationException {
		if(accountService == null){
			throw new IllegalStateException("accountService can't be null");
		}
		
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		String accountName = token.getUsername();
		
		
		if( accountName != null && !"".equals(accountName) ){
			Account account = accountService.getAcount(accountName );
	
			if( account != null ){
				return new SimpleAuthenticationInfo(
						account.getUsername(),account.getPassword(), getName() );
			}
		}

		return null;
	}
	
}
