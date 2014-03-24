package com.tmser.core.exception;

/**
 * 版本冲突异常
 * @author 张凯
 * @date 2014-2-11
 */
public class VersionConflictException extends BaseRuntimeException {

	private static final long serialVersionUID = 1L;
	
	public VersionConflictException(String message) {
		super(message);
	}

}
