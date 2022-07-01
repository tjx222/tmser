package com.tmser.blog.model.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.EqualsAndHashCode;

/**
 * SheetMeta entity.
 *
 * @author ryanwang
 * @author ikaisec
 * @date 2019-08-04
 */
@Entity(name = "SheetMeta")
@DiscriminatorValue("1")
@EqualsAndHashCode(callSuper = true)
public class SheetMeta extends BaseMeta {

    /**
     * type
     */
    @Column(name = "type", nullable = false)
    private Integer type = 1;

}
