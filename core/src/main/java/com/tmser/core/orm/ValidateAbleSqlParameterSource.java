package com.tmser.core.orm;

import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;


/**
 * 带验证功能的，sqlParameterSource
 * @author tjx
 * @version 2.0
 * 2014-1-20
 */
public class ValidateAbleSqlParameterSource extends BeanPropertySqlParameterSource{

	private Table table;
	
	public ValidateAbleSqlParameterSource(Object object) {
		super(object);
	}

	public ValidateAbleSqlParameterSource(Object object,Table t) {
		super(object);
		this.table = t;
	}
	
	public ValidateAbleSqlParameterSource withTable(Table t){
		this.table = t;
		return this;
	}
	
	  /**
	   * {@inheritDoc}
	   */	
	@Override
	public boolean hasValue(String paramName){
		return table.getColumn(paramName) != null;
	}
	 /**
	   * {@inheritDoc}
	   */	
	@Override
	public int getSqlType(String paramName){
		return StatementCreatorUtils.javaTypeToSqlParameterType((Class<?>)table.getColumn(paramName).getAttrType());
	}
	
/**
 * {@inheritDoc}
 */	
	@Override
	public Object getValue(String paramName) throws IllegalArgumentException {
		if(table == null){
			throw new NullPointerException("table must be set befor this method invoke!" );
		}
		Column column = table.getColumn(paramName);
		Object value = null;
		if(column != null){
			value = super.getValue(column.getName());
			ValidateUtils.checkColumnValue(column,value);
		}else{
			throw new IllegalArgumentException("this table mapper bo doesn't has the parameter :"+paramName );
		}
		
		return value;
	}
}
