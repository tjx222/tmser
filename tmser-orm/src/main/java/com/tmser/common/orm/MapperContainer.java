package com.tmser.common.orm;


/**
 * ORM 配置容器
 * 系统配置
 * @author tjx
 * @version 2.0
 * 2014-1-15
 */
public interface MapperContainer {
	
	/**
	 * 根据 Bo 的 class 查找 Table信息
	 * @param className 类简单名称，全系统必须保证bo 的简单名唯一
	 * @return
	 */
	Table getTable(String className);
	
	/**
	 * 注册Bo 映射
	 * @param className
	 * @param table
	 */
	void addTable(String className,Table table);
	
	/**
	 * 清空容器中的映射
	 */
	void clear();
	
	/**
	 * 包含的映射总数
	 * @return
	 */
	int size();
}
