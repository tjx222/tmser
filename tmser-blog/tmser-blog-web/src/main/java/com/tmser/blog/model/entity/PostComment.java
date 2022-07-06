package com.tmser.blog.model.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * PostComment entity.
 *
 * @author johnniang
 */
@Entity(name = "PostComment")
@DiscriminatorValue(PostComment.CT_POST+"")
@Table(name = "comments", indexes = {
        @Index(name = "comments_post_id", columnList = "post_id"),
        @Index(name = "comments_type_status", columnList = "type, status"),
        @Index(name = "comments_parent_id", columnList = "parent_id")})
@Data
@ToString(callSuper = true)
public class PostComment extends BaseComment {
    private Integer type = CT_POST;
}
