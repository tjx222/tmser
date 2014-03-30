package com.tmser.core.bo;

import java.io.Serializable;

import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.tmser.core.orm.OrderAble;
import com.tmser.core.orm.page.Page;
import com.tmser.core.orm.page.PageAble;


public abstract class QueryObject implements Serializable,PageAble,OrderAble{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3559826418950495733L;

	/**
	 * 分页参数
	 */
	@Transient
	private Page page = new Page();
	
	/**
	 * 自定义排序
	 */
	@Transient 
	private String tmserOrder;
	
	
	/**
	 * 自定义查询字段
	 */
	@Transient 
	private String customCulomn;

	public void currentPage(int currentPage) {
		page.setCurrentPage(currentPage);
	}

	public void pageSize(int pageSize) {
		page.setPageSize(pageSize);
	}

	/**
	 * 分页参数
	 */
	@Override
	public Page page() {
		return page;
	}

	public void addPage(Page page) {
		this.page = page;
	}

	
	public String customCulomn() {
		return customCulomn;
	}

	public void addCustomCulomn(String customCulomn) {
		this.customCulomn = customCulomn;
	}
	
	/**
	 * 自定义排序
	 */
	public String order() {
		return tmserOrder;
	}

	/**
	 * 自定义排序
	 */
	public void addOrder(String order) {
		this.tmserOrder = order;
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	
	public abstract boolean equals(Object obj);

	public abstract int hashCode();
}
