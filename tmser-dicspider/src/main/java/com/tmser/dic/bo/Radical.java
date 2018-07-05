/**
 * Mainbo.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.tmser.dic.bo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.tmser.common.bo.QueryObject;

/**
 * <pre>
 *  部首
 * </pre>
 *
 * @author tjx1222
 * @version $Id: Radical.java, v 1.0 2017年1月24日 下午10:39:26 tjx1222 Exp $
 */
@SuppressWarnings("serial")
@Entity
@Table(name = Radical.TABLE_NAME)
public class Radical extends QueryObject{
	
	public static final String TABLE_NAME = "radical";
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;

	/**
	 *部首内容
	 **/
	@Column(name="content",length=1)
	private String content;

	/**
	 *部首壁画数
	 **/
	@Column(name="bihuashu")
	private Integer bihuashu;

	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getBihuashu() {
		return bihuashu;
	}

	public void setBihuashu(Integer bihuashu) {
		this.bihuashu = bihuashu;
	}

	@Override
	public boolean equals(final Object other) {
			if (!(other instanceof Radical))
				return false;
			Radical castOther = (Radical) other;
			return new EqualsBuilder().append(id, castOther.id).isEquals();
	}

	@Override
	public int hashCode() {
			return new HashCodeBuilder().append(id).toHashCode();
	}

}
