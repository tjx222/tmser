/**
 * 
 */
package com.tmser.core.orm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表信息
 * @author tjx
 * @version 2.0
 * 2014-1-15
 */
public class Table {
	/**
	 * 表bo中映射名
	 */
	private String name;
	
	private String pkName;
	
	private String catalog = "";
	
	private String schema = "" ;
	/**
	 * 表名
	 */
	private String tableName = "id";
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	
	public String getPkName() {
		return pkName;
	}

	public void setPkName(String pkName) {
		this.pkName = pkName;
	}


	/**
	 * 包含的字段名 - 字段信息 映射
	 */
	private Map<String,Column> columnMap = new HashMap<String, Column>();
	
	/**
	 * 表的bo 属性名称 - 字段信息 映射
	 */
	private Map<String,Column> attrMap = new HashMap<String, Column>();
	
	/**
	 * 添加字段包装类信息
	 * @param columnName 字段名称
	 * @param column 字段对应的栏目
	 */
	public void addColumn(String columnName,Column column){
		columnMap.put(columnName, column);
		attrMap.put(column.getName(), column);
	}
	
	/**
	 * 根据表字段名获取改字段信息
	 * @param columnName
	 * @return
	 */
	public Column getColumn(String columnName){
		if(columnName == null)
			return null;
		
		return columnMap.get(columnName.toUpperCase());
	}
	
	/**
	 * 获取表包含的所有字段信息
	 * @return
	 */
	public List<Column> getColumns(){
		List<Column> columns = new ArrayList<Column>(Collections.unmodifiableCollection(columnMap.values()));
		return columns;
	}
	
	/**
	 * 根据bo 属性名获取栏目
	 * @param attrName bo 属性名
	 * @return
	 */
	public Column getColumnByAttrName(String attrName){
		return attrMap.get(attrName);
	}
	
	/**
	 * 获取表包含字段数
	 * @return
	 */
	public int size(){
		return columnMap.size();
	}
}
