/**
 * 
 */
package com.tmser.core.orm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * bo映射容器类
 * @author tjx
 * @version 2.0
 * 2014-1-15
 */
@Component("tspMapperContainer")
public class DefaultMapperContainer implements MapperContainer{
	
	private final static Map<String,Table> tableMap = new HashMap<String, Table>();
	  /**
	   * {@inheritDoc}
	   */
	@Override
	public Table getTable(String className) {
		return tableMap.get(className);
	}
	  /**
	   * {@inheritDoc}
	   */	
	@Override
	public void addTable(String className,Table table){
		tableMap.put(className, table);
	}
	  /**
	   * {@inheritDoc}
	   */
	@Override
	public void clear() {
		tableMap.clear();
	}
	  /**
	   * {@inheritDoc}
	   */
	@Override
	public int size() {
		return tableMap.size();
	}
}
