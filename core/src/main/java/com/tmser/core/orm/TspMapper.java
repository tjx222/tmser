package com.tmser.core.orm;

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
public abstract class TspMapper<T> implements RowMapper<T>{
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
