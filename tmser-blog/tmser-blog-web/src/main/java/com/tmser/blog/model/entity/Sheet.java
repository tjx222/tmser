package com.tmser.blog.model.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Page entity.
 *
 * @author johnniang
 * @date 3/22/19
 */
@Entity(name = "Sheet")
@Table(name = "posts", indexes = {
        @Index(name = "posts_type_status", columnList = "type, status"),
        @Index(name = "posts_create_time", columnList = "create_time")})
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.INTEGER,
        columnDefinition = "int default 0")
@DiscriminatorValue("1")
@Data
public class Sheet extends BasePost {

    @Column(name = "type", nullable = false)
    private Integer type = 1;

}
