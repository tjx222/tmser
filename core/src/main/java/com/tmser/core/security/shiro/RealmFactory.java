package com.tmser.core.security.shiro;

import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.realm.Realm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tmser.core.constants.ConsForSystem;
import com.tmser.core.utils.CacheLoader;

/**
 * 
 * @author tjx
 * @date 2014-03-03
 * 
 */
public class RealmFactory {
	
	private static Logger log = LoggerFactory.getLogger(RealmFactory.class);
	
	public static final String REALM_KEY_JDBC = "jdbc";
	
	public static final String REALM_KEY_CAS = "cas";
	
	private String casParamKey = ConsForSystem.SYS_PARAM_CAS;
	
	private String useCasKey = ConsForSystem.SYS_PARAM_CAS_SWITCH;
	
	private boolean useCas;
	
	private Map<String,Realm> realms = new HashMap<String,Realm>();

	public void setRealms(Map<String, Realm> realms) {
		this.realms = realms;
	}
	
	/**
	 * Realm key
	 */
	protected String getRealmKey( ){
		if(isUseCas())
			return REALM_KEY_CAS;
		
		return REALM_KEY_JDBC;
	}
	
	public boolean isUseCas(){
		String useCasString = CacheLoader.getCache(casParamKey,useCasKey,true).toString();
		if("true".equalsIgnoreCase(useCasString) ||
				"1".equalsIgnoreCase(useCasString)){
			useCas = true;
		}
		return useCas;
	}

	public Realm createRealm(){
		String key = getRealmKey();
		Realm realm = realms.get( key );
		if( realm == null ){
			log.error("Shiro Realm[{}]未实现！", key );
		}else{
			log.info("Shiro Realm[{}] 初始化完成！",key);
		}
		return realm;
	}

	public void setCasParamKey(String casParamKey) {
		this.casParamKey = casParamKey;
	}

	public void setUseCasKey(String useCasKey) {
		this.useCasKey = useCasKey;
	}

	public void setUseCas(boolean useCas) {
		this.useCas = useCas;
	}
}
