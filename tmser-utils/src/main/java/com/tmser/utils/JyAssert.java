package com.tmser.utils;

import org.apache.commons.lang3.StringUtils;

import com.tmser.utils.exception.BaseException;

/**
 * 教研平台对象判定，判定为假则抛出错误
 * @author wanzheng
 *
 */
public class JyAssert {
	
	/**
	 * 非空判定，为空则抛异常
	 * @param obj
	 * @param errorMsg
	 */
	public static void notNull(Object obj,String errorMsg){
		if(obj==null){
			throw new BaseException(errorMsg);
		}
	}
	
	/**
	 * 为真判定，非空则抛异常
	 * @param b
	 * @param errorMsg
	 */
	public static void isTrue(Boolean b,String errorMsg){
		if(b==null||!b){
			throw new BaseException(errorMsg);
		}
	}
	
	public static void isTrue(Boolean b,String errorMsg,Object... args){
		if(b==null||!b){
			throw new BaseException(errorMsg,args);
		}
	}
	
	/**
	 * 非空判定，为空则抛移除
	 * @param str str!=null && str包含非空字符
	 * @param string
	 */
	public static void notBlank(String str, String errorMsg) {
		if(StringUtils.isBlank(str)){
			throw new BaseException(errorMsg);
		}
		
	}
}
