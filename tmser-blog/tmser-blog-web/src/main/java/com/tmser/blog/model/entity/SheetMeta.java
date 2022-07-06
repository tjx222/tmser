package com.tmser.blog.model.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * SheetMeta entity.
 *
 * @author ryanwang
 * @author ikaisec
 * @date 2019-08-04
 */
@Entity(name = "SheetMeta")
@DiscriminatorValue(SheetMeta.MT_SHEET+"")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Table(name = "metas")
public class SheetMeta extends BaseMeta {

    /**
     * type
     */
    @Column(name = "type", nullable = false)
    private Integer type = MT_SHEET;

}
