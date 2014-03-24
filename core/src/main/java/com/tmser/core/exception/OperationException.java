package com.tmser.core.exception;

/**
 * 操作异常，主要记录一些业务操作异常，供页面展示，并记录到异常日志表中
 * @author 张凯
 * @date 2014-2-20
 */
public class OperationException extends BaseRuntimeException {

	private static final long serialVersionUID = -7522817908022938045L;

	public OperationException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 带有操作类型构造器
	 * @param operType 操作类型
	 * @param message
	 */
	public OperationException(String operType, String message) {
		super(message);
	}

}
