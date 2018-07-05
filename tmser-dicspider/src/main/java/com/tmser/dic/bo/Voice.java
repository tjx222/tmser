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
@Table(name = Voice.TABLE_NAME)
public class Voice extends QueryObject{
	
	public static final String TABLE_NAME = "word_voice";
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;

	/**
	 *拼音id
	 **/
	@Column(name="pinyin_id")
	private Integer pinyinId;

	/**
	 *拼音声调
	 **/
	@Column(name="shengdiao")
	private Integer shengdiao;
	
	@Column(name="voice")
	private byte[] voice;

	
	public Integer getId() {
		return id;
	}


	public Integer getPinyinId() {
		return pinyinId;
	}


	public void setPinyinId(Integer pinyinId) {
		this.pinyinId = pinyinId;
	}


	public Integer getShengdiao() {
		return shengdiao;
	}


	public void setShengdiao(Integer shengdiao) {
		this.shengdiao = shengdiao;
	}


	public byte[] getVoice() {
		return voice;
	}


	public void setVoice(byte[] voice) {
		this.voice = voice;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	@Override
	public boolean equals(final Object other) {
			if (!(other instanceof Voice))
				return false;
			Voice castOther = (Voice) other;
			return new EqualsBuilder().append(id, castOther.id).isEquals();
	}

	@Override
	public int hashCode() {
			return new HashCodeBuilder().append(id).toHashCode();
	}

}
