/**
 * 
 */
package com.tmser.core.page;

/**
 * Mysql 分页拼接工具实现
 * @author tjx
 * @version 2.0
 * 2014-1-17
 */
public class MysqlPageSqlHelper implements PageSqlHelper{
		@Override
		public String build(String sql, Page page) {
			    int pagesize = page.getPageSize() + (page.needTotal() ? 0:1);
		        StringBuilder pageSql = new StringBuilder(100);
		        String beginrow = String.valueOf((page.getCurrentPage() - 1) * page.getPageSize());
		        pageSql.append(sql);
		        pageSql.append(" limit ").append(beginrow).append(",").append(pagesize);
		  return pageSql.toString();
		}

		@Override
		public String dbType() {
			return "mysql";
		}
}
