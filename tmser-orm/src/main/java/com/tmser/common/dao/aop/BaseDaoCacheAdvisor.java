/**
 * Mainbo.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.tmser.common.dao.aop;


import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;

import com.tmser.common.bo.QueryObject;
import com.tmser.common.cache.BaseCacheAspect;
import com.tmser.common.dao.BaseDAO;
import com.tmser.common.orm.MapperContainer;
import com.tmser.common.orm.Table;
import com.tmser.utils.Reflections;

/**
 * <pre>
 *	dao 层缓存器
 *  依赖 @see MapperContainer
 * </pre>
 *
 * @author tmser
 * @version $Id: BaseDaoCacheInterceptor.java, v 1.0 2015年2月16日 下午1:44:45 tmser Exp $
 */
public class BaseDaoCacheAdvisor extends BaseCacheAspect{
	/**
	 * 是否开启缓存
	 */
	private boolean openCache = true;
	
	/**
	 * ORM 配置容器
	 */
	@Autowired
	protected MapperContainer mapperContainer;
	
	public boolean isOpenCache() {
		return openCache;
	}

	public void setOpenCache(boolean openCache) {
		this.openCache = openCache;
	}
	
	public MapperContainer getMapperContainer() {
		return mapperContainer;
	}

	public void setMapperContainer(MapperContainer mapperContainer) {
		this.mapperContainer = mapperContainer;
	}
	
	@SuppressWarnings("unchecked")
	public <T>T findFromCache(ProceedingJoinPoint pjp)  throws Throwable{
		Class<?> entity =((BaseDAO<?,?>)pjp.getTarget()).thisBoClass();
		String name = entity.getName();
		String methodName = pjp.getSignature().getName();
        Object arg = pjp.getArgs().length >= 1 ? pjp.getArgs()[0] : null;
		
		String key = "";
		Object obj = null;
        if ("get".equals(methodName)) {
            key = String.valueOf(arg);
        	obj = get(name,key);
	        //cache hit
	        if (obj != null) {
	        	logger.debug("cacheName:{}, hit key:{}", name, key);
	            return (T)obj;
	        }
	        logger.debug("cacheName:{}, miss key:{}", name, key);
        }
        
        //cache miss
        obj =  pjp.proceed();
        
        if("delete".equals(methodName)){
        	 key = String.valueOf(arg);
        	 this.evict(name, key);
        }
       
        if("update".equals(methodName)){
        	 Table t =  mapperContainer.getTable(entity.getSimpleName());
        	 Object id = null;
			try {
					id = Reflections.getFieldValue(arg, t.getColumn(t.getPkName()).getName());
				} catch (Exception e) {
					logger.warn("update cache failed", e);
				}
        	 if(id != null){
        		 this.evict(name, id.toString());
        	 }
        }
        
        if(obj instanceof QueryObject){
        	 Table t =  mapperContainer.getTable(entity.getSimpleName());
            //put cache
            put(name,Reflections.getFieldValue(obj, t.getColumn(t.getPkName()).getName()).toString(),obj);
        }

        return (T)obj;
	}
	
}
