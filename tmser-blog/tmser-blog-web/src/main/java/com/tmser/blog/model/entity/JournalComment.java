package com.tmser.blog.model.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Journal comment.
 *
 * @author johnniang
 * @date 2019-04-25
 */
@Entity(name = "JournalComment")
@DiscriminatorValue("2")
@Data
@ToString
public class JournalComment extends BaseComment {

    private Integer type = 2;
}
