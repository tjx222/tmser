package com.tmser.core.orm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.tmser.core.bo.BaseObject;

/**
 * Mapper 工厂
 * 负责创建并缓存基本类map
 * 
 * @author tjx
 * @version 2.0
 * 2014-1-22
 */
public class MapperFactory {
 
	private final static Map<Class<?>,TspMapper<?>> MAPPERS = new HashMap<Class<?>,TspMapper<?>>();
	/**
	 * 根据bo类型创建 TspMapper
	 * @param c
	 * @return
	 */
	public static <T extends BaseObject> TspMapper<T> create(Class<T> c){
		return  getMapper(c);
	}
	
	/**
	 * 根据 BO 类型创建，对应的TspMapper 
	 * @param entity
	 * @return
	 */
	public static <T extends BaseObject> TspMapper<T> getMapper(
			final Class<? extends BaseObject> entity) {
		TspMapper<T> mapper = (TspMapper<T>) MAPPERS.get(entity);
		if(mapper != null){
			return mapper;
		}
		
		synchronized(MAPPERS){
			if(mapper == null){
				mapper = new TspMapper<T>() {
					@SuppressWarnings("unchecked")
					@Override
					public T map(ResultSet rs, int rowNum) throws SQLException {
						T bo = (T) OrmHelper.getEntity(entity);
						bo = (T) OrmHelper.mapperBean(rs, entity);
						return bo;
					}
				};
				
				MAPPERS.put(entity, mapper);
			}
			
			return mapper;
		}
		
	}
}
