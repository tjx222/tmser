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
@Table(name = Word.TABLE_NAME)
public class Word extends QueryObject{
	
	public static final String TABLE_NAME = "word";
	
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
	 * 笔画数
	 */
	@Column(name="bi_hua_shu")
	private Integer biHuaShu;

	/**
	 * 部首
	 */
	@Column(name="radical",length=1)
	private String radical;
	
	/**
	 * 部首id
	 */
	@Column(name="radical_id")
	private Integer radicalId;
	
	/**
	 * 常用等级
	 */
	@Column(name="used_level")
	private Integer usedLevel;
	
	/**
	 * 五笔
	 */
	@Column(name="wu_bi")
	private String wuBi;
	
	/**
	 * 笔顺
	 */
	@Column(name="bi_shun",length=64)
	private String biShun;
	
	/**
	 * 基本解释
	 */
	@Column(name="basic_desc",length=Integer.MAX_VALUE)
	private String basicDesc;
	
	/**
	 * 详细解释
	 */
	@Column(name="detail_desc",length=Integer.MAX_VALUE)
	private String detailDesc;
	
	@Column(name="is_duo_yin_zi")
	private Boolean isDuoYinZi;
	
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

	public Integer getBiHuaShu() {
		return biHuaShu;
	}

	public void setBiHuaShu(Integer biHuaShu) {
		this.biHuaShu = biHuaShu;
	}

	public Integer getUsedLevel() {
		return usedLevel;
	}

	public void setUsedLevel(Integer usedLevel) {
		this.usedLevel = usedLevel;
	}

	public String getRadical() {
		return radical;
	}

	public void setRadical(String radical) {
		this.radical = radical;
	}

	public Integer getRadicalId() {
		return radicalId;
	}

	public void setRadicalId(Integer radicalId) {
		this.radicalId = radicalId;
	}

	public String getWuBi() {
		return wuBi;
	}

	public void setWuBi(String wuBi) {
		this.wuBi = wuBi;
	}

	public String getBiShun() {
		return biShun;
	}

	public void setBiShun(String biShun) {
		this.biShun = biShun;
	}

	public String getBasicDesc() {
		return basicDesc;
	}

	public void setBasicDesc(String basicDesc) {
		this.basicDesc = basicDesc;
	}

	public String getDetailDesc() {
		return detailDesc;
	}

	public void setDetailDesc(String detailDesc) {
		this.detailDesc = detailDesc;
	}

	public Boolean getIsDuoYinZi() {
		return isDuoYinZi;
	}

	public void setIsDuoYinZi(Boolean isDuoYinZi) {
		this.isDuoYinZi = isDuoYinZi;
	}

	@Override
	public boolean equals(final Object other) {
			if (!(other instanceof Word))
				return false;
			Word castOther = (Word) other;
			return new EqualsBuilder().append(id, castOther.id).isEquals();
	}

	@Override
	public int hashCode() {
			return new HashCodeBuilder().append(id).toHashCode();
	}

}
