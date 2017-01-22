/**
 * Mainbo.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.tmser.common.orm.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Component;

import com.tmser.common.cache.BaseCacheAspect;
import com.tmser.utils.Encodes;

/**
 * <pre>
 * sql mapping 缓存器
 * </pre>
 *
 * @author tmser
 * @version $Id: SqlMappingCacheAspact.java, v 1.0 2015年2月7日 下午4:19:22 tmser Exp $
 */

@Component
@Aspect
public class SqlMappingCacheAspact extends BaseCacheAspect implements InitializingBean{
	
	@Value("#{config.getProperty('sqlmapping_cachename','sqlMappingCache')}")
	private String cacheName = "sqlMappingCache";
	
	private Cache cache;
	
    public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	/**
     * 匹配用户Service
     */
    @Pointcut(value = "target(com.tmser.common.orm.SqlMapping)")
    private void sqlMappingPointcut() {
    }
    
    @Around(value = "sqlMappingPointcut()")
	public String aroundSqlMapping(ProceedingJoinPoint pjp) throws Throwable{
	        Object arg = pjp.getArgs().length >= 1 ? pjp.getArgs()[0] : null;
	        String sql = null;
	        if(arg != null){
	        	String key = String.valueOf(arg);
	        	sql = get(cacheName,Encodes.encodeBase64(key.getBytes()));
	        	
	        	if(sql != null){
	        		logger.debug("cacheName:{}, hit key:{}", cacheName, key);
	        		return sql;
	        	}
	        	
		        logger.debug("cacheName:{}, miss key:{}", cacheName, key);
		        //cache miss
		        sql = (String) pjp.proceed();
		        //put cache
		        put(cacheName,Encodes.encodeBase64(key.getBytes()),sql);
	        }
	    return sql == null ? "" : sql;    
	}

    @Override
	protected Cache getCache(String cacheName) {
		return this.cache;
	}
    
	/**
	 * @throws Exception
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		this.cache = getCacheManager().getCache(cacheName);
	}
}
