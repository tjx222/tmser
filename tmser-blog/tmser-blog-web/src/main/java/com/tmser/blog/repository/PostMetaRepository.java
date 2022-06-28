package com.tmser.blog.repository;

import com.tmser.blog.model.entity.PostMeta;
import com.tmser.blog.repository.base.BaseMetaRepository;
import org.apache.ibatis.annotations.Mapper;

/**
 * PostMeta repository.
 *
 * @author ryanwang
 * @author ikaisec
 * @author guqing
 * @date 2019-08-04
 */
@Mapper
public interface PostMetaRepository extends BaseMetaRepository<PostMeta> {

}
