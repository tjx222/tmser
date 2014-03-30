package com.tmser.core.orm.search;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.tmser.core.bo.BaseObject;
import com.tmser.core.orm.ColumnObtainer;


/**
 * sql 语句解析器
 * 
 * @author tjx
 * @version 2.0
 * 2014-1-23
 */
public class SearchSqlHelper {

	
	public static final String COLUMN_PRFIX = "#";
	
	public static final String TABLE_PRFIX = "@";
	
	static final String DEFAULT_ALIAS = "__default";
	
	static final String AS = "as";
	
	static final Pattern COLUMN_PATTERN = Pattern.compile("#([a-zA-Z_]+\\.)?[a-zA-Z_]+");
	
	static final Pattern TABLE_PATTERN = Pattern.compile("@([a-zA-Z_]+\\.)?[a-zA-Z]+");
	
	static final Pattern TABLE_ALAIS_PATTERN = Pattern.compile("@([a-zA-Z_]+\\.)?[a-zA-Z]+\\s*[a-zA-Z]*\\s*[a-zA-Z_]*");
	
	static final List<String> SQLWORDS = new ArrayList<String>();
	
	
	static {
		SQLWORDS.add("SET");
		SQLWORDS.add("UNION");
		SQLWORDS.add("JOIN");
		SQLWORDS.add("WHERE");
		SQLWORDS.add("ORDER");
		SQLWORDS.add("GROUP");
		SQLWORDS.add("LEFT");
		SQLWORDS.add("RIGHT");
		SQLWORDS.add("INNER");
		SQLWORDS.add("FULL");
	}
	
	/**
	 * 解析中变量名
	 * 
	 * @param sql 要解析的sql
	 * @param columnObtainers 栏目解析器
	 * @return
	 */
	
	public static String parseSql(String sql,ColumnObtainer ...columnObtainers ){
		if(StringUtils.isBlank(sql) || (!sql.contains(COLUMN_PRFIX)
				&& !sql.contains(TABLE_PRFIX))){
			return sql;
		}
		
		Map<String,ColumnObtainer> columnMap = new HashMap<String,ColumnObtainer>();
		for(ColumnObtainer c : columnObtainers){
			if(StringUtils.isBlank(c.getAlias())){
				columnMap.put(c.getType().getName(), c);
			}else{
				columnMap.put(c.getAlias(), c);
			}
		}
		
		String sb = parseColumn(sql,columnMap);
		
		return parseTableName(sb, columnObtainers);
		
	}
	
	/**
	 * 解析中变量名
	 * 
	 * @param sql 要解析的sql,要求是完整的sql
	 * @param clsess 栏目解析器
	 * @return
	 */
	
	public static String parseSql(String sql,Class<? extends BaseObject> ...clsses ){
		if(StringUtils.isBlank(sql) || (!sql.contains(COLUMN_PRFIX)
				&& !sql.contains(TABLE_PRFIX))){
			return sql;
		}
		
		Map<String,List<String>>aliasMap = parseTableAlisName(sql);
		
		Map<String,ColumnObtainer> columnMap = new HashMap<String,ColumnObtainer>();
		for(Class<? extends BaseObject> c : clsses){
			List<String> aliasList = aliasMap.get("@"+c.getSimpleName());
			if(aliasList == null){
				aliasList = aliasMap.get("@"+c.getName());
			}
			if(aliasList != null){
					for(String alias :aliasList){
					if(alias == DEFAULT_ALIAS){//只能一个表不设别名
						columnMap.put(DEFAULT_ALIAS, ColumnObtainer.build(c).newInstance());
					}else{
						columnMap.put(alias, ColumnObtainer.build(c).alias(alias).newInstance());
					}
				}
			}
		}
		
		String sb = parseColumn(sql,columnMap);
		
		return parseTableName(sb,columnMap.values().toArray(new ColumnObtainer[columnMap.size()]));
		
	}
	
	
	/**
	 * 解析sql变量名，可直接不需要完整的sql
	 * 
	 * @param sql 要解析的sql
	 * @param clsess 栏目解析器
	 * @return
	 */
	
	public static String parseHalfSql(String sql,ColumnObtainer... clsses){
		if(StringUtils.isBlank(sql) || (!sql.contains(COLUMN_PRFIX)
				&& !sql.contains(TABLE_PRFIX))){
			return sql;
		}
		
		Map<String,ColumnObtainer> columnMap = new HashMap<String,ColumnObtainer>();
		for(ColumnObtainer c : clsses){
			String alias = c.getAlias();
			columnMap.put(StringUtils.isBlank(alias)?DEFAULT_ALIAS:alias, c);
		}
		
		String sb = parseColumn(sql,columnMap);
		
		return parseTableName(sb,columnMap.values().toArray(new ColumnObtainer[columnMap.size()]));
		
	}
	
	static String parseColumn(String sql,Map<String,ColumnObtainer> columnMap){
		StringBuffer sb = new StringBuffer();
		Matcher m = COLUMN_PATTERN.matcher(sql);
		String columnName = "";
		while(m.find()) {
			columnName = getColumnName(m.group(),columnMap);
			if(StringUtils.isNotBlank(columnName)){
				 m.appendReplacement(sb, columnName); 
			}
	    }
		m.appendTail(sb);
		
		return sb.toString();
	}
	
	static String parseTableName(String sql,ColumnObtainer[] columnObtainers){
		StringBuffer sb = new StringBuffer();
		Matcher m = TABLE_PATTERN.matcher(sql);
		String columnName = "";
		while(m.find()) {
			columnName = getTableName(m.group(),columnObtainers);
			if(StringUtils.isNotBlank(columnName)){
				 m.appendReplacement(sb, columnName); 
			}
	    }
		m.appendTail(sb);
		
		return sb.toString();
	}
	
	static Map<String,List<String>> parseTableAlisName(String sql){
		Map<String,List<String>> aliasMap = new HashMap<String, List<String>>();
		Matcher m = TABLE_ALAIS_PATTERN.matcher(sql);
		while(m.find()) {
			setAlias(m.group(),aliasMap);
	    }
		return aliasMap;
	}
	
	static void setAlias(String group,Map<String,List<String>> map){
		String[] names = group.split("\\s+");
		String alias = null;
		String classname = names[0];
		List<String> aliasList = map.get(classname);
		switch(names.length){
		case 1:
			alias = DEFAULT_ALIAS;
			break;
		case 2:
			alias = SQLWORDS.contains(names[1].toUpperCase())?DEFAULT_ALIAS:names[1];
			break;
		case 3:
			alias = AS.equals(names[1].toUpperCase())?names[2]:
				SQLWORDS.contains(names[1].toUpperCase())?DEFAULT_ALIAS:names[1];
			break;
		default: break;
		}
		
		if(aliasList == null){
			aliasList = new ArrayList<String>();
			map.put(classname, aliasList);
		}
		aliasList.add(alias);
	}
	
	static String getColumnName(final String group,Map<String,ColumnObtainer> columnObtainers){
		
		String s = group.replace(COLUMN_PRFIX, "");
		
		String[] names = s.split("\\.");
		
		String prefix = names.length > 1 ? names[0] : "";
		
		String name = names.length > 1 ? names[1] : names[0];
		
		ColumnObtainer c = columnObtainers.get(StringUtils.isBlank(prefix)?DEFAULT_ALIAS:prefix);
		
		String columnName = null;
		
		if(c == null && !"".equals(prefix)){
			throw new IllegalStateException("Doesn't have a ColumnObtainer with the prefix["+prefix+"].");
		}
		
		if(c != null)
			columnName = c.getColumn(name);
		
/*  遍历查询，如存在同名字段将导致未知错误
 * 		for(String key : columnObtainers.keySet()){
			c = columnObtainers.get(key);
			columnName = c.getColumn(name);
			if(columnName != null){
				break;
			}
		}*/
		
		if(columnName == null){
			throw new IllegalStateException("Doesn't have a column[ "+ name +" ] in the tables.");
		}
		
		return columnName;
		
	}
	
	static String getTableName(String group, ColumnObtainer[] columnObtainers){
		String s = group.replace(TABLE_PRFIX, "");
		
		String tableName = null;
		
		for(ColumnObtainer c : columnObtainers){
			if(s.equals(c.getType().getName()) || s.equals(c.getType().getSimpleName())){
				tableName = c.getTableName();
			}
			if(tableName != null)
				break;
		}
		
		return tableName;
	}
	
}
