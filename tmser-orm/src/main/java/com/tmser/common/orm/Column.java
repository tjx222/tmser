package com.tmser.common.orm;


import java.lang.reflect.Type;

import javax.persistence.GenerationType;

/**
 * 字段属性类
 * @author tjx
 * @version 2.0
 * 2014-1-15
 */
public class Column {

	/**
	 * 字段数据库名称
	 */
	private String column;
	/**
	 * 字段bo 中名称
	 */
	private String name;
	/**
	 * 字段是否允许为null,默认为true
	 */
	private boolean nullable = true;
	/**
	 * 字段是否是唯一标识,默认为false
	 */
	private boolean unique;
	/**
	 * length:表示该字段的大小,仅对String类型的字段有效
	 */
	private int length;
	
	/**
	 * 精度
	 */
	private int scale;
	
	private boolean isPK = false;
	
	/**
	 * 是否自动增长，目前只支持主键
	 */
	private boolean isAutoIncrement = false;
	
	private GenerationType generationType;
	
	/**
	 * bo 字段属性类型
	 */
	private Type attrType;
	
	/**
	 * 创建脚本生成时使用
	 * 表示该字段在数据库中的实际类型.通常ORM框架可以根据属性类型自动判断数据库中字段的类型,
	 * 但是对于Date类型仍无法确定数据库中字段类型究竟是DATE,TIME还是TIMESTAMP.此外,
	 * String的默认映射类型为VARCHAR,如果要将String类型映射到特定数据库的BLOB或TEXT字段类型,
	 * 该属性非常有用.
	 */
	private String columnDefinition;
	
	public boolean isAutoIncrement() {
		return isAutoIncrement;
	}

	public void setAutoIncrement(boolean isAutoIncrement) {
		this.isAutoIncrement = isAutoIncrement;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isNullable() {
		return nullable;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public boolean isUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getColumnDefinition() {
		return columnDefinition;
	}

	public void setColumnDefinition(String columnDefinition) {
		this.columnDefinition = columnDefinition;
	}

	public boolean isPK() {
		return isPK;
	}

	public void setPK(boolean isPK) {
		this.isPK = isPK;
	}

	public Type getAttrType() {
		return attrType;
	}

	public void setAttrType(Type attrType) {
		this.attrType = attrType;
	}

	/** 
	 * Getter method for property <tt>generationType</tt>. 
	 * @return property value of generationType 
	 */
	public GenerationType getGenerationType() {
		return generationType;
	}

	/**
	 * Setter method for property <tt>generationType</tt>.
	 * @param generationType value to be assigned to property generationType
	 */
	public void setGenerationType(GenerationType generationType) {
		this.generationType = generationType;
	}
	
	
}
