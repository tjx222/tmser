package com.tmser.common.orm;

import org.springframework.jdbc.core.RowMapper;

/**
 * mapper 创建工具
 * @author jxtan
 * @date 2014年12月5日
 */
public interface MapperCreater {

	/**
	 * 根据实体类型创建映射 
	 * @param entity
	 * @return
	 */
	<T> RowMapper<T> createMapper(final Class<?> entity);
}
