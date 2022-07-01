package com.tmser.blog.model.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * PostComment entity.
 *
 * @author johnniang
 */
@Entity(name = "PostComment")
@DiscriminatorValue("0")
@Data
@ToString
public class PostComment extends BaseComment {
    private Integer type = 0;
}
