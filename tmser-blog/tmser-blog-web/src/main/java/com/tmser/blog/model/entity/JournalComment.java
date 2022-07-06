package com.tmser.blog.model.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * Journal comment.
 *
 * @author johnniang
 * @date 2019-04-25
 */
@Entity(name = "JournalComment")
@Table(name = "comments", indexes = {
        @Index(name = "comments_post_id", columnList = "post_id"),
        @Index(name = "comments_type_status", columnList = "type, status"),
        @Index(name = "comments_parent_id", columnList = "parent_id")})
@DiscriminatorValue(JournalComment.CT_JOUR+"")
@Data
@ToString(callSuper = true)
public class JournalComment extends BaseComment {
    private Integer type = CT_JOUR;
}
