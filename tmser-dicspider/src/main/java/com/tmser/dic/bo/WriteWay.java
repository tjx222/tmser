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
 *  拼音
 * </pre>
 *
 * @author tjx1222
 * @version $Id: Radical.java, v 1.0 2017年1月24日 下午10:39:26 tjx1222 Exp $
 */
@SuppressWarnings("serial")
@Entity
@Table(name = WriteWay.TABLE_NAME)
public class WriteWay extends QueryObject{
	
	public static final String TABLE_NAME = "word_write_way";
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;

	/**
	 *拼音id
	 **/
	@Column(name="word_id")
	private Integer wordId;

	/**
	 *写法内容大小
	 **/
	@Column(name="size")
	private Integer size;
	
	/**
	 *文件格式
	 **/
	@Column(name="ext")
	private String ext;
	
	@Column(name="data")
	private byte[] data;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getWordId() {
		return wordId;
	}

	public void setWordId(Integer wordId) {
		this.wordId = wordId;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	@Override
	public boolean equals(final Object other) {
			if (!(other instanceof WriteWay))
				return false;
			WriteWay castOther = (WriteWay) other;
			return new EqualsBuilder().append(id, castOther.id).isEquals();
	}

	@Override
	public int hashCode() {
			return new HashCodeBuilder().append(id).toHashCode();
	}

}
