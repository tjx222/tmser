package com.tmser.blog.model.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * Sheet comment.
 *
 * @author johnniang
 * @date 19-4-24
 */
@Entity(name = "SheetComment")
@Table(name = "comments", indexes = {
        @Index(name = "comments_post_id", columnList = "post_id"),
        @Index(name = "comments_type_status", columnList = "type, status"),
        @Index(name = "comments_parent_id", columnList = "parent_id")})
@DiscriminatorValue(SheetComment.CT_SHEET +"")
@Data
@ToString
public class SheetComment extends BaseComment {
    private Integer type = CT_SHEET;
}
