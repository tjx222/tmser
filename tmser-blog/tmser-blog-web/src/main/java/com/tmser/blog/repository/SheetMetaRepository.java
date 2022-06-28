package com.tmser.blog.repository;

import com.tmser.blog.model.entity.SheetMeta;
import com.tmser.blog.repository.base.BaseMetaRepository;
import org.apache.ibatis.annotations.Mapper;

/**
 * SheetMeta repository.
 *
 * @author ryanwang
 * @author ikaisec
 * @date 2019-08-04
 */
@Mapper
public interface SheetMetaRepository extends BaseMetaRepository<SheetMeta> {
}
