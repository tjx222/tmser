package com.tmser.blog.model.params;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import com.tmser.blog.model.entity.SheetMeta;

/**
 * Sheet meta param.
 *
 * @author ryanwang
 * @author ikaisec
 * @date 2019-08-04
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SheetMetaParam extends BaseMetaParam<SheetMeta> {
}
