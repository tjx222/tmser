package com.tmser.blog.model.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * PostMeta entity.
 *
 * @author ryanwang
 * @author ikaisec
 * @date 2019-08-04
 */
@Entity(name = "PostMeta")
@DiscriminatorValue(PostMeta.MT_POST+"")
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@Table(name = "metas")
public class PostMeta extends BaseMeta {

    private Integer type = MT_POST;
}
