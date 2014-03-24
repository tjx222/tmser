package com.tmser.core.bo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.tmser.core.page.Page;
import com.tmser.core.page.PageAware;
/**
 * 
 * 
 * @author tjx
 */
public abstract class BaseObject implements PageAware,Serializable {
	
	private static final long serialVersionUID = 4237270095441710555L;

	/**
	 * 分页参数
	 */
	@Transient
	private Page page = new Page();
	
	/**
	 * 扩展属性
	 */
	@Transient
	private String flags = "";
	
	/**
	 * 扩展属性
	 */
	@Transient
	private String flago = "";
	
	/**
	 * 自定义排序
	 */
	@Transient 
	private String tspOrder;
	
	/**
	 * 创建人Id
	 */
	@Column(name="crt_id")
	private String crtId;

	/**
	 * 创建时间
	 */
	@Column(name="crt_dttm")
	private Date crtDttm;

	/**
	 * 最后修改人ID
	 */
	@Column(name="lastup_id")
	private String lastupId;

	/**
	 * 最后修改时间
	 */
	@Column(name="lastup_dttm")
	private Date lastupDttm;

	/**
	 * 有效性
	 */
	@Column(name="enable_flg")
	private String enableFlg;


	/**
	 * 自定义排序
	 */
	public String tspOrder() {
		return tspOrder;
	}

	/**
	 * 自定义排序
	 */
	public void setTspOrder(String tspOrder) {
		this.tspOrder = tspOrder;
	}

	/**
	 * 分页参数s
	 */
	public String getFlags() {
		return flags;
	}

	/**
	 * 扩展属性s
	 */
	public void setFlags(String flags) {
		this.flags = flags;
	}

	/**
	 * 扩展属性o
	 */
	public String getFlago() {
		return flago;
	}

	/**
	 * 扩展属性o
	 */
	public void setFlago(String flago) {
		this.flago = flago;
	}

	/**
	 * 分页参数
	 */
	public Page getPage() {
		return page;
	}

	/**
	 * 分页参数
	 */
	public void setPage(Page page) {
		this.page = page;
	}
	
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
	public String getCrtId() {
		return crtId;
	}

	/**
	 * 创建人Id
	 */
	public void setCrtId(String crtId) {
		this.crtId = crtId;
	}

	/**
	 * 最后修改人ID
	 */
	public String getLastupId() {
		return lastupId;
	}

	/**
	 * 最后修改人ID
	 */
	public void setLastupId(String lastupId) {
		this.lastupId = lastupId;
	}

	/**
	 * 有效性
	 */
	public String getEnableFlg() {
		return enableFlg;
	}

	/**
	 * 有效性
	 */
	public void setEnableFlg(String enableFlg) {
		this.enableFlg = enableFlg;
	}

	/**
	 * 设计分页参数
	 * @param pageSize
	 * @param currentPage
	 */
    public void pageParameter(int pageSize,int currentPage){
    	if(page != null){
    		page.reset(pageSize, currentPage);
    	}
    }


	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	
	public abstract boolean equals(Object obj);

	public abstract int hashCode();
	
}