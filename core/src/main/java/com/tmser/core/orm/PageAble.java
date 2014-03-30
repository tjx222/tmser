package com.tmser.core.orm;

import com.tmser.core.orm.page.Page;

/**
 * 分页接口
 * @author tjx
 * 2013-12-30
 * v2.0
 */
public interface PageAble {

	/**
	 * 获取分页对象
	 * @return
	 */
	Page getPage();

}
