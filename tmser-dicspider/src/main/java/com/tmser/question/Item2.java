
package com.tmser.question;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.tmser.common.bo.QueryObject;

/**
 * <pre>
 *
 * </pre>
 *
 * @author tmser
 * @version $Id: Item.java, v 1.0 2019年7月22日 上午11:30:18 tmser Exp $
 */
@Entity
@Table(name = Item2.TABLE_NAME)
public class Item2 extends QueryObject {

  /**
   * <pre>
   *
   * </pre>
   */
  private static final long serialVersionUID = 5851155076447701188L;

  public static final String TABLE_NAME = "item2";

  @Id
  @Column(name = "id")
  private Integer id;

  /**
   *
   **/
  @Column(name = "content", length = Integer.MAX_VALUE)
  private String content;

  /**
   * 父节点id
   **/
  @Column(name = "answer", length = Integer.MAX_VALUE)
  private String answer;

  /**
   * 邮政编码
   */
  @Column(name = "exp", length = Integer.MAX_VALUE)
  private String exp;

  @Column(name = "source_id")
  private String sourceId;

  @Column(name = "item_bank_id")
  private Integer bankId;

  @Column(name = "item_type_id")
  private Integer typeId;

  @Column(name = "difficult")
  private Integer difficult;

  @Column(name = "option_count")
  private Integer optionCount;

  @Column(name = "item_source_id")
  private Integer sourceTypeId;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * Getter method for property <tt>content</tt>.
   *
   * @return content String
   */
  public String getContent() {
    return content;
  }

  /**
   * Setter method for property <tt>content</tt>.
   *
   * @param content String value to be assigned to property content
   */
  public void setContent(String content) {
    this.content = content;
  }

  /**
   * Getter method for property <tt>answer</tt>.
   *
   * @return answer String
   */
  public String getAnswer() {
    return answer;
  }

  /**
   * Setter method for property <tt>answer</tt>.
   *
   * @param answer Integer value to be assigned to property answer
   */
  public void setAnswer(String answer) {
    this.answer = answer;
  }

  /**
   * Getter method for property <tt>exp</tt>.
   *
   * @return exp String
   */
  public String getExp() {
    return exp;
  }

  /**
   * Setter method for property <tt>exp</tt>.
   *
   * @param exp String value to be assigned to property exp
   */
  public void setExp(String exp) {
    this.exp = exp;
  }

  /**
   * Getter method for property <tt>sourceId</tt>.
   *
   * @return sourceId String
   */
  public String getSourceId() {
    return sourceId;
  }

  /**
   * Setter method for property <tt>sourceId</tt>.
   *
   * @param sourceId String value to be assigned to property sourceId
   */
  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }

  /**
   * Getter method for property <tt>bankId</tt>.
   *
   * @return bankId Integer
   */
  public Integer getBankId() {
    return bankId;
  }

  /**
   * Setter method for property <tt>bankId</tt>.
   *
   * @param bankId Integer value to be assigned to property bankId
   */
  public void setBankId(Integer bankId) {
    this.bankId = bankId;
  }

  /**
   * Getter method for property <tt>typeId</tt>.
   *
   * @return typeId Integer
   */
  public Integer getTypeId() {
    return typeId;
  }

  /**
   * Setter method for property <tt>typeId</tt>.
   *
   * @param typeId Integer value to be assigned to property typeId
   */
  public void setTypeId(Integer typeId) {
    this.typeId = typeId;
  }

  /**
   * Getter method for property <tt>difficult</tt>.
   *
   * @return difficult Integer
   */
  public Integer getDifficult() {
    return difficult;
  }

  /**
   * Setter method for property <tt>difficult</tt>.
   *
   * @param difficult Integer value to be assigned to property difficult
   */
  public void setDifficult(Integer difficult) {
    this.difficult = difficult;
  }

  /**
   * Getter method for property <tt>optionCount</tt>.
   *
   * @return optionCount Integer
   */
  public Integer getOptionCount() {
    return optionCount;
  }

  /**
   * Setter method for property <tt>optionCount</tt>.
   *
   * @param optionCount Integer value to be assigned to property optionCount
   */
  public void setOptionCount(Integer optionCount) {
    this.optionCount = optionCount;
  }

  /**
   * Getter method for property <tt>sourceTypeId</tt>.
   *
   * @return sourceTypeId Integer
   */
  public Integer getSourceTypeId() {
    return sourceTypeId;
  }

  /**
   * Setter method for property <tt>sourceTypeId</tt>.
   *
   * @param sourceTypeId Integer value to be assigned to property sourceTypeId
   */
  public void setSourceTypeId(Integer sourceTypeId) {
    this.sourceTypeId = sourceTypeId;
  }

  @Override
  public boolean equals(final Object other) {
    if (!(other instanceof Item2))
      return false;
    Item2 castOther = (Item2) other;
    return new EqualsBuilder().append(id, castOther.id).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(id).toHashCode();
  }

}
