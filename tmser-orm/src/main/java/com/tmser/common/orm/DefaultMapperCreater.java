package com.tmser.common.orm;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;


/**
 * Mapper 工厂
 * 负责创建并缓存基本类map
 * 
 * @author tjx
 * @version 2.0
 * 2014-1-22
 */
public class DefaultMapperCreater implements MapperCreater{
 
	private final static Map<Class<?>,AbstractMapper<?>> MAPPERS = new HashMap<Class<?>,AbstractMapper<?>>();
	
	/**
	 * 根据 BO 类型创建，对应的AbstractMapper 
	 * @param entity
	 * @return
	 */
	public static <T> AbstractMapper<T> getMapper(
			final Class<?> entity) {
		@SuppressWarnings("unchecked")
		AbstractMapper<T> mapper = (AbstractMapper<T>) MAPPERS.get(entity);
		if(mapper != null){
			return mapper;
		}
		
		synchronized(MAPPERS){
			if(mapper == null){
				mapper = new AbstractMapper<T>() {
					@SuppressWarnings("unchecked")
					@Override
					public T map(ResultSet rs, int rowNum) throws SQLException {
						T bo = (T)OrmHelper.mapperBean(rs,rowNum,entity,primitivesDefaultedForNullValue);
						return bo;
					}
				};
				
				MAPPERS.put(entity, mapper);
			}
			
			return mapper;
		}
		
	}

	@Override
	public <T> RowMapper<T> createMapper(
			Class<?> entity) {
		return  getMapper(entity);
	}

}
