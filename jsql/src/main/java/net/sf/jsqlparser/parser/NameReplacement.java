package net.sf.jsqlparser.parser;

/**
 * 表名及字段名解析替换
 * @author jxtan
 * @date 2015年1月8日
 */
public interface NameReplacement {

	/**
	 * 替换表名
	 * @param tablename
	 * @return
	 */
	String replaceTableName(String tablename);
	
	/**
	 * 替换字段名
	 * @return
	 */
	String replaceColumnName(String tableName,String columnName);
	
	/**
	 * 替换别名
	 * @return
	 */
	String replaceAlias(String alias);
}
