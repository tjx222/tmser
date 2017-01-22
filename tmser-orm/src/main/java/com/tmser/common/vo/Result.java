package com.tmser.common.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * 结果类
 * 
 * @author jxtan
 * @date 2014年12月2日
 */
public class Result implements Serializable {

	/**
	 * <pre>
	 *
	 * </pre>
	 */
	private static final long serialVersionUID = -6002530010670165167L;

	private int code;

	private String msg;

	private Date optime;

	private Object data;

	public static final Result newInstance() {
		return new Result();
	}

	/**
	 * 默认成功
	 */
	public Result() {
		this.code = 1;
	}

	/**
	 * 错误信息结果
	 * 
	 * @param msg
	 */
	public Result(String msg) {
		this.msg = msg;
	}

	/**
	 * 返回消息
	 * 
	 * @param msg
	 * @param isError
	 *            是否时错误消息
	 */
	public Result(String msg, Boolean isError) {
		this.msg = msg;
		if (!isError) {
			code = 1;
		}
	}

	/**
	 * 成功结果
	 * 
	 * @param data
	 */
	public Result(Object data) {
		this.code = 1;
		this.data = data;
	}

	/**
	 * 成功结果(有时间的返回)
	 * 
	 * @param data
	 */
	public Result(Integer code, String msg, Date optime, Object data) {
		this.code = code;
		this.msg = msg;
		this.optime = optime;
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Serializable data) {
		this.data = data;
	}

	public Date getOptime() {
		return optime;
	}

	public void setOptime(Date optime) {
		this.optime = optime;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
