package com.tmser.common.bo;

import com.tmser.common.page.Page;



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
	
	/**
	 * 是否需要将page 对象输出为json
	 * 默认为false,即不输出
	 * @return
	 */
	boolean needParseToJson();

}
