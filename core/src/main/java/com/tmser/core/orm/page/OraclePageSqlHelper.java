package com.tmser.core.orm.page;


/**
 *
 * @author tjx
 * @version 2.0
 * 2014-1-17
 */
public class OraclePageSqlHelper implements PageSqlHelper{
	public static final String ORACLE_DBNAME = "oracle";
	  /**
     * 参考hibernate的实现完成oracle的分页
     * 
     * @param sql
     * @param page
     * @return String
     */
    public String build(String sql, Page page) {
        StringBuilder pageSql = new StringBuilder(100);
        String beginrow = String.valueOf((page.getCurrentPage() - 1) * page.getPageSize());
        String endrow = String.valueOf(page.getCurrentPage() * page.getPageSize() + (page.needTotal() ? 0:1));
        pageSql.append("select * from ( select temp.*, rownum row_id from ( ");
        pageSql.append(sql);
        pageSql.append(" ) temp where rownum <= ").append(endrow);
        pageSql.append(") where row_id > ").append(beginrow);
        return pageSql.toString();
    }

	@Override
	public String dbType() {
		return ORACLE_DBNAME;
	}
}