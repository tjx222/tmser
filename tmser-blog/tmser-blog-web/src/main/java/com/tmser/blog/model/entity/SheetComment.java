package com.tmser.blog.model.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Sheet comment.
 *
 * @author johnniang
 * @date 19-4-24
 */
@Entity(name = "SheetComment")
@DiscriminatorValue("1")
@Data
@ToString
public class SheetComment extends BaseComment {

    private Integer type = 1;
}
