
package com.tmser.core.exception;

/**
 * 泛型未设置异常
 * @author tjx
 * @version 2.0
 * 2014-1-15
 */
public class PersistentNotSetException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4592253202142777391L;

	public PersistentNotSetException(String msg){
		super(msg);
	}
}
