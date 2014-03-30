package com.tmser.core.orm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.tmser.core.bo.QueryObject;

/**
 * Mapper 工厂
 * 负责创建并缓存基本类map
 * 
 * @author tjx
 * @version 2.0
 * 2014-1-22
 */
public class MapperFactory {
 
	private final static Map<Class<?>,TmserMapper<?>> MAPPERS = new HashMap<Class<?>,TmserMapper<?>>();
	/**
	 * 根据bo类型创建 TspMapper
	 * @param c
	 * @return
	 */
	public static <T extends QueryObject> TmserMapper<T> create(Class<T> c){
		return  getMapper(c);
	}
	
	/**
	 * 根据 BO 类型创建，对应的TspMapper 
	 * @param entity
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends QueryObject> TmserMapper<T> getMapper(
			final Class<? extends QueryObject> entity) {
		TmserMapper<T> mapper = (TmserMapper<T>) MAPPERS.get(entity);
		if(mapper != null){
			return mapper;
		}
		
		synchronized(MAPPERS){
			if(mapper == null){
				mapper = new TmserMapper<T>() {
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
