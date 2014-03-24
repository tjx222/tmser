package com.tmser.core.page;

/**
 * 分页接口
 * @author tjx
 * 2013-12-30
 * v2.0
 */
public interface PageAware {
	/**
	 * 获取分页
	 * @return
	 */
	Page getPage();
	

	/**
	 * 设置分页
	 * @param page
	 */
	void setPage(Page page);

}
