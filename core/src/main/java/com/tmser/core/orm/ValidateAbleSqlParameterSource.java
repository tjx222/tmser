package com.tmser.core.orm;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;

import com.tmser.core.exception.ParamIllegalityException;

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
	public Object getValue(String paramName) throws IllegalArgumentException {
		if(table == null){
			throw new NullPointerException("table must be set befor this method invoke!" );
		}
		Object value = super.getValue(paramName);
			try {
				Column column = table.getColumnByAttrName(paramName);
				if(column != null){
					ValidateUtils.checkColumnValue(column,value);
					//throw new IllegalArgumentException("this table mapper bo doesn't has the parameter :"+paramName );
				}
				
			} catch (ParamIllegalityException e) {
				new IllegalArgumentException(e.getMessage());
			}
		
		return value;
	}
}
