package com.tmser.core.orm;

import java.util.List;

import com.tmser.core.bo.QueryObject;
import com.tmser.core.utils.StringUtils;


/**
 * 字段名称获取器
 * @author tjx
 * @version 2.0
 * 2014-1-22
 */
public class ColumnObtainer {
	
	/**
	 * 创建字段名称获取器
	 * @param c
	 * @param alias
	 * @return
	 */
	public static class Builder {
		private Class<? extends QueryObject> type;
		
		private String alias;
		
		private String split = ",";
		
		public Builder(Class<? extends QueryObject> type){
			this.type = type;
		}
		
		public Builder(Class<? extends QueryObject> type,String alias){
			this.type = type;
			this.alias = alias;
		}
		
		public Builder alias(String alias){
			this.alias = alias;
			return this;
		}
	
		public Builder split(String split){
			this.split = split;
			return this;
		}
		
		public ColumnObtainer newInstance(){
			return new ColumnObtainer(this);
		}
	}
	
	
	private Class<? extends QueryObject> type;
	
	private String alias;
	
	private String split = ",";
	private Table t;
	
	public ColumnObtainer(Builder builder){
		this.type = builder.type;
		this.alias  =  builder.alias;
		this.split = builder.split;
		this.t = OrmHelper.getTable(type);
	}
	
	public static Builder build(Class<? extends QueryObject> type){
		return new Builder(type);
	}
	
	/**
	 * 获取单个属性名对应的字段名称
	 * @param attrName 属性名
	 * @return
	 */
	public String getColumn(String attrName){
		return getColumn(attrName, alias);
	}
	
	/**
	 * 获取多个属性
	 * @param attrNames 属性民称数组
	 * @return
	 */
	
	public String getColumns(String... attrNames){
		String rs = "";
		StringBuilder sb = new StringBuilder();
		for(String attr : attrNames){
			sb.append(getColumn(attr, alias)).append(split);
		}
		if(sb.length() > 0 ){
			if(split != null && split.length() > 0){
				rs = sb.substring(0, sb.length()-split.length());
			}
		}
		return rs;
	}
	
	/**
	 * 获取该bo 建立了对应的所有字段
	 * @return
	 */
	public String getAllColumns(){
		if(type == null){
			throw new IllegalStateException("type must be set before this method invoke");
		}
		if(split ==  null)
			split = "";
		
		String rs = "";
		StringBuilder sb = new StringBuilder();
		
		if(t != null){
			List<Column> cols = t.getColumns();
			String al = "";
			if(StringUtils.isNotBlank(alias)){
				al = alias+".";
			}
			for(Column col : cols){
				if(col != null){
					sb.append(al).append(col.getColumn()).append(split);
				}
			}
		}
		
		if(sb.length() > 0 ){
			if(split != null && split.length() > 0){
				rs = sb.substring(0, sb.length()-split.length());
			}
		}
		return rs;
	}
	
	/**
	 * 自定义别名获取，字段
	 * @param attrName
	 * @param alias
	 * @return
	 */
	protected String getColumn(String attrName, String alias){
		if(type == null){
			throw new IllegalStateException("type must be set before this method invoke");
		}
		
		return parseColumnName(type, attrName, alias);
	}
	
	
	/**
	 * 获取bo属性的表中字段名
	 * 
	 * @param c bo 类型
	 * @param attrName 属性名称
	 * @param alias 自定义的查询别名
	 * @return
	 */
	protected  String parseColumnName(Class<?> c,String attrName,String alias){
		StringBuilder name = new StringBuilder();
		if(t != null){
			Column col = t.getColumnByAttrName(attrName);
			if(col != null){
				if(StringUtils.isNotBlank(alias)){
					name.append(alias).append(".");
				}
				name.append(col.getColumn());
			}
		}
		
		return name.toString();
	}
	
	public String getAlias(){
		return this.alias;
	}
	
	public Class<? extends QueryObject> getType(){
		return this.type;
	}
	
	public ColumnObtainer changeSplit(String split){
		this.split = split;
		return this;
	}
	
	public String getTableName(){
		return t.getTableName();
	}
}
