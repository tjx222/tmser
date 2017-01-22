package com.tmser.common.bo;


/**
 * 可自定义排序
 * @author tjx
 * @version 2.0
 * 2014-3-25
 */
public interface OrderAble {

	/**
	 * 使用模型查询时，自定义排序语句
	 * @return 
	 */
	 String order();
}
