package com.tmser.blog.model.entity;

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
@DiscriminatorValue(value = "0")
public class Post extends BasePost {

}