package com.tmser.blog.model.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Post entity.
 *
 * @author johnniang
 */
@Entity(name = "Post")
@Table(name = "posts", indexes = {
        @Index(name = "posts_type_status", columnList = "type, status"),
        @Index(name = "posts_create_time", columnList = "create_time")})
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.INTEGER,
        columnDefinition = "int default 0")
@DiscriminatorValue(value = BasePost.T_POST+"")
@Data
public class Post extends BasePost {
    private Integer type = T_POST;
}
