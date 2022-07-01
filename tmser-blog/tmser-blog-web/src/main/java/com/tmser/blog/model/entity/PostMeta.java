package com.tmser.blog.model.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

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
@DiscriminatorValue("0")
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class PostMeta extends BaseMeta {

    private Integer type = 0;
}
