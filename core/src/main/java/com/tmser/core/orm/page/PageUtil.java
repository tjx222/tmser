package com.tmser.core.orm.page;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tmser.core.utils.SpringContextHolder;

/**
 * 分页公共方法
 * @author tjx
 * 2013-12-30
 * v2.0
 */
public class PageUtil
{
	public static final PageSqlHelper DEFUALT_PAGESQLHELPER = new MysqlPageSqlHelper();
	public static final Logger log = LoggerFactory.getLogger(PageUtil.class);
	
	/**
	 * 
	 * 使用结果列表及分页信息组装列表
	 * @param list
	 * @param page
	 * @return
	 */
	public static <T>  PageList<T> createPageList(List<T> list,Page page){
		return new PageList<T>(list,page);
	}

	/**
	 * 
	 * @param list
	 * @param pageAware
	 * @return
	 */
	public static <T>  PageList<T> createPageList(List<T> list,PageAble pageAble){
		return new PageList<T>(list,pageAble.page());
	}
	
	
	/**
	 * 组装分页语句
	 * @param sql 需要分页的语句
	 * @param page 分页信息
	 */
	public static String gernatePageSql(String sql, Page page){
		PageSqlHelper psh = SpringContextHolder.getBean(PageSqlHelper.class);
		if(psh == null){
			log.warn("not found the PageSqlHelper implememt bean use the default mysql implements");
			psh = DEFUALT_PAGESQLHELPER;
		}
		return psh.build(sql, page);
	}
	
}