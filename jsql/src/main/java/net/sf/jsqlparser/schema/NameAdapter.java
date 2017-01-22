package net.sf.jsqlparser.schema;

import net.sf.jsqlparser.parser.DefaultNameReplacement;
import net.sf.jsqlparser.parser.NameReplacement;

public class NameAdapter {
	
	private static NameReplacement nameReplacement = new DefaultNameReplacement();
	
	public static void setNameReplacement(NameReplacement nameReplacement){
		if(nameReplacement != null)
			NameAdapter.nameReplacement = nameReplacement;
	}
	public static String replaceTableName(String tablename) {
		return nameReplacement.replaceTableName(tablename);
	}

	public static String replaceColumnName(String tableName,String columnName) {
		return nameReplacement.replaceColumnName(tableName,columnName);
	}

	public static String replaceAlias(String alias) {
		return nameReplacement.replaceAlias(alias);
	}

}
