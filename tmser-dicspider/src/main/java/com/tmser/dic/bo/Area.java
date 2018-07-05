/**
 * Mainbo.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.tmser.dic.bo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.tmser.common.bo.QueryObject;

/**
 * <pre>
 *  单字拼音
 * </pre>
 *
 * @author tjx1222
 * @version $Id: Radical.java, v 1.0 2017年1月24日 下午10:39:26 tjx1222 Exp $
 */
@SuppressWarnings("serial")
@Entity
@Table(name = Area.TABLE_NAME)
public class Area extends QueryObject{
	
	public static final String TABLE_NAME = "sys_area";
	
	@Id
	@Column(name="id")
	private Integer id;

	/**
	 *
	 **/
	@Column(name="name",length=32)
	private String name;
	
	
	/**
	 *父节点id
	 **/
	@Column(name="parent_id")
	private Integer parentId;
	
	/**
	 *层级
	 **/
	@Column(name="level")
	private Integer level;

	/**
	 *排序
	 **/
	@Column(name="sort")
	private Integer sort;
	
	/**
	 * 邮政编码
	 */
	@Column(name="postcode")
	private String postcode;
	
	/**
	 * 区域编码
	 */
	@Column(name="code")
	private String code;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public boolean equals(final Object other) {
			if (!(other instanceof Area))
				return false;
			Area castOther = (Area) other;
			return new EqualsBuilder().append(id, castOther.id).isEquals();
	}

	@Override
	public int hashCode() {
			return new HashCodeBuilder().append(id).toHashCode();
	}

}
