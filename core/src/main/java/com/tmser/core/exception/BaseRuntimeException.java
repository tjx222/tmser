package com.tmser.core.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基础UnCheckedException，作为所有自定义异常基类
 * @author 张凯
 * @date 2014-2-11
 */
public abstract class BaseRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	protected final Logger log = LoggerFactory.getLogger(getClass());

	public BaseRuntimeException(String message) {
		super(message);
	}
}
