package com.tmser.common.orm;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * 封装springjdbc RowMapp
 * 
 * @author tjx
 * @version 2.0
 * 2014-1-24
 */
public abstract class AbstractMapper<T> implements RowMapper<T>{
	
	/** Whether we're defaulting primitives when mapping a null value */
	protected boolean primitivesDefaultedForNullValue = false;
	
	/**
	 * Set whether we're defaulting Java primitives in the case of mapping a null value
	 * from corresponding database fields.
	 * <p>Default is {@code false}, throwing an exception when nulls are mapped to Java primitives.
	 */
	public void setPrimitivesDefaultedForNullValue(boolean primitivesDefaultedForNullValue) {
		this.primitivesDefaultedForNullValue = primitivesDefaultedForNullValue;
	}

	/**
	 * Return whether we're defaulting Java primitives in the case of mapping a null value
	 * from corresponding database fields.
	 */
	public boolean isPrimitivesDefaultedForNullValue() {
		return primitivesDefaultedForNullValue;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public T mapRow(ResultSet rs, int rowNum) throws SQLException
	{
		return map(rs,rowNum);
	}
	
	/**
	 * 根据ResultSet 填充对象并返回
	 * 
	 * @param rs 查询结果集
	 * @param rowNum 当然结果行数
	 * @return 
	 * @throws SQLException
	 */
	public abstract T map(ResultSet rs,int rowNum) throws SQLException ;
}
