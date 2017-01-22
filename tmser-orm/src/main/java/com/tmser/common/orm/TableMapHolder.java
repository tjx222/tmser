package com.tmser.common.orm;

import java.util.Map;

public class TableMapHolder {
	private static ThreadLocal<Map<String,String>> tableMap = new ThreadLocal<Map<String,String>>();
	
	
	public static void setTableMap(Map<String,String> value) {
		tableMap.set(value);
	}

	public static Map<String,String> getTableMap() {
		return tableMap.get();
	}
	
	public static void clear(){
		tableMap.remove();
	}
}
