package com.tmser.core.bo;

import java.util.Date;

/**
 * 日志类
 * 
 * @author jfyang
 * @version 2.0
 * 2014-01-26
 *
 */
public class LogObject {

	/**
	 * 操作人Id
	 */
	private String userid;
	
	/**
	 * 操作人所属区域编码
	 */
	private Integer areaCode;
	
	/**
	 * 操作人所属组织机构（包括学校）编码
	 */
	private Integer orgCode;
	
	/**
	 * 操作描述
	 */
	private String logInfo;
	
	/**
	 * 所属模块
	 */
	private String moduleCode;
	
	/**
	 * 操作时间
	 */
	private Date createDate;
	
	/**
	 * 客户端Ip
	 */
	private String clientIp;
	
	/**
	 * 保留字段0
	 */
	private String reserved0;
	
	/**
	 * 保留字段1
	 */
	private String reserved1;
	
	/**
	 * 保留字段2
	 */
	private String reserved2;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public Integer getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(Integer areaCode) {
		this.areaCode = areaCode;
	}

	public Integer getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(Integer orgCode) {
		this.orgCode = orgCode;
	}

	public String getLogInfo() {
		return logInfo;
	}

	public void setLogInfo(String logInfo) {
		this.logInfo = logInfo;
	}

	public String getModuleCode() {
		return moduleCode;
	}

	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getReserved0() {
		return reserved0;
	}

	public void setReserved0(String reserved0) {
		this.reserved0 = reserved0;
	}

	public String getReserved1() {
		return reserved1;
	}

	public void setReserved1(String reserved1) {
		this.reserved1 = reserved1;
	}

	public String getReserved2() {
		return reserved2;
	}

	public void setReserved2(String reserved2) {
		this.reserved2 = reserved2;
	}
	
}
