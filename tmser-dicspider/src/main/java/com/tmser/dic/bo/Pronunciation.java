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
 *  单字拼音
 * </pre>
 *
 * @author tjx1222
 * @version $Id: Radical.java, v 1.0 2017年1月24日 下午10:39:26 tjx1222 Exp $
 */
@SuppressWarnings("serial")
@Entity
@Table(name = Pronunciation.TABLE_NAME)
public class Pronunciation extends QueryObject{
	
	public static final String TABLE_NAME = "pronunciation";
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;

	/**
	 *字
	 **/
	@Column(name="word",length=1)
	private String word;
	
	
	/**
	 *拼音首字母
	 **/
	@Column(name="shou_zi_mu",length=1)
	private String shouZiMu;
	
	/**
	 *拼音id
	 **/
	@Column(name="pin_yin_id")
	private Integer pinYinId;

	/**
	 *字id
	 **/
	@Column(name="word_id")
	private Integer wordId;
	
	/**
	 * 拼音，不带声调
	 */
	@Column(name="pin_yin")
	private String pinYin;
	
	/**
	 * 拼音，带声调
	 */
	@Column(name="du_yin")
	private String duYin;
	
	/**
	 * 声调
	 */
	@Column(name="sheng_diao")
	private Integer shengDiao;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getShouZiMu() {
		return shouZiMu;
	}

	public void setShouZiMu(String shouZiMu) {
		this.shouZiMu = shouZiMu;
	}

	public Integer getPinYinId() {
		return pinYinId;
	}

	public void setPinYinId(Integer pinYinId) {
		this.pinYinId = pinYinId;
	}

	public Integer getWordId() {
		return wordId;
	}

	public void setWordId(Integer wordId) {
		this.wordId = wordId;
	}

	public String getPinYin() {
		return pinYin;
	}

	public void setPinYin(String pinYin) {
		this.pinYin = pinYin;
	}

	public String getDuYin() {
		return duYin;
	}

	public void setDuYin(String duYin) {
		this.duYin = duYin;
	}

	public Integer getShengDiao() {
		return shengDiao;
	}

	public void setShengDiao(Integer shengDiao) {
		this.shengDiao = shengDiao;
	}

	@Override
	public boolean equals(final Object other) {
			if (!(other instanceof Pronunciation))
				return false;
			Pronunciation castOther = (Pronunciation) other;
			return new EqualsBuilder().append(id, castOther.id).isEquals();
	}

	@Override
	public int hashCode() {
			return new HashCodeBuilder().append(id).toHashCode();
	}

}
