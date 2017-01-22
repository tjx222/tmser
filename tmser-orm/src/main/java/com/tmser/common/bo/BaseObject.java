package com.tmser.common.bo;

import java.util.Date;

import javax.persistence.Column;

import org.springframework.format.annotation.DateTimeFormat;


/**
 * 
 * 
 * @author tjx
 */
public abstract class BaseObject extends QueryObject {
	
	private static final long serialVersionUID = 4237270095441710555L;
	
	/**
	 * 生效
	 */
	
	public static final int ENABLE = 1;
	
	/**
	 * 失效
	 */
	public static final int DISABLE = 0;

	/**
	 * 创建人Id
	 */
	@Column(name="crt_id")
	private Integer crtId;

	/**
	 * 创建时间
	 */
	@Column(name="crt_dttm")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date crtDttm;

	/**
	 * 最后修改人ID
	 */
	@Column(name="lastup_id")
	private Integer lastupId;

	/**
	 * 最后修改时间
	 */
	@Column(name="lastup_dttm")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lastupDttm;

	/**
	 * 有效性
	 */
	@Column(name="enable")
	private Integer enable;

	/**
	 * 创建时间
	 */
	public Date getCrtDttm() {
		return crtDttm;
	}

	/**
	 * 创建时间
	 */
	public void setCrtDttm(Date crtDttm) {
		this.crtDttm = crtDttm;
	}

	/**
	 * 最后修改时间
	 */
	public Date getLastupDttm() {
		return lastupDttm;
	}

	/**
	 * 最后修改时间
	 */
	public void setLastupDttm(Date lastupDttm) {
		this.lastupDttm = lastupDttm;
	}

	/**
	 * 创建人Id
	 */
	public Integer getCrtId() {
		return crtId;
	}

	/**
	 * 创建人Id
	 */
	public void setCrtId(Integer crtId) {
		this.crtId = crtId;
	}

	/**
	 * 最后修改人ID
	 */
	public Integer getLastupId() {
		return lastupId;
	}

	/**
	 * 最后修改人ID
	 */
	public void setLastupId(Integer lastupId) {
		this.lastupId = lastupId;
	}

	/**
	 * 有效性
	 */
	public Integer getEnable() {
		return enable;
	}

	/**
	 * 有效性
	 */
	public void setEnable(Integer enable) {
		this.enable = enable;
	}

}