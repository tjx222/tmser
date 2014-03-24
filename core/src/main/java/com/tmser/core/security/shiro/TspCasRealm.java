package com.tmser.core.security.shiro;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cas.CasRealm;
import org.apache.shiro.subject.PrincipalCollection;

import com.tmser.core.constants.ConsForSystem;
import com.tmser.core.security.PermissionFit;
import com.tmser.core.security.RoleFit;
import com.tmser.core.utils.CacheLoader;


/**
 * CAS Realm 实现
 * @author tjx
 * 2014-03-01
 */
public class TspCasRealm extends CasRealm{
	/**
	 * 权限获取器
	 */
	private PermissionFit permissionFit;
	
	/**
	 * 角色及角色权限获取器
	 */
	private RoleFit roleFit;
	
	private String casParamKey = ConsForSystem.SYS_PARAM_CAS;
	
	private String casServerUrlPrefixKey = ConsForSystem.SYS_PARAM_WSDL_CAS;
	
	private String casServiceKey = ConsForSystem.SYS_PARAM_CAS_SERVICE_URL;
	
	/**
	 * 授权信息
	 */
	@Override
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
	
	@Override
    public String getCasServerUrlPrefix() {
        return CacheLoader.getCache(casParamKey, casServerUrlPrefixKey ,true).toString();
    }

    @Override
    public String getCasService() {
    	return CacheLoader.getCache(casParamKey, casServiceKey, true).toString();
    }

	public void setPermissionFit(PermissionFit permissionFit) {
		this.permissionFit = permissionFit;
	}

	public void setRoleFit(RoleFit roleFit) {
		this.roleFit = roleFit;
	}

	public void setCasServerUrlPrefixKey(String casServerUrlPrefixKey) {
		this.casServerUrlPrefixKey = casServerUrlPrefixKey;
	}

	public void setCasServiceKey(String casServiceKey) {
		this.casServiceKey = casServiceKey;
	}

	public void setCasParamKey(String casParamKey) {
		this.casParamKey = casParamKey;
	}

}
