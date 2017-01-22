package net.sf.jsqlparser.parser;

public class DefaultNameReplacement implements NameReplacement{

	@Override
	public String replaceTableName(String tablename) {
		return tablename;
	}

	@Override
	public String replaceColumnName(String tableName,String columnName) {
		return columnName;
	}

	@Override
	public String replaceAlias(String alias) {
		return alias;
	}

}
