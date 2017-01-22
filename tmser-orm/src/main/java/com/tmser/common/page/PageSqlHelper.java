package com.tmser.common.page;


/**
 * 分页语句拼接辅助类
 *
 * @author tjx
 * @version 2.0
 * 2014-1-17
 */
public interface PageSqlHelper {

		/**
		 * 扩展原有sql,增加分页功能
		 * 需要根据dbType 选择实现类
		 * @param old 原有sql
		 * @param p 分页参数
		 * @return
		 */
		String build(String sql,Page page);
		
		/**
		 * 语句适应的数据库类型标示
		 * @return
		 */
		String dbType();
		
}
