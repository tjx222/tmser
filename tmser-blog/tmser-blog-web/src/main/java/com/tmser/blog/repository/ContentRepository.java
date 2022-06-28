package com.tmser.blog.repository;

import com.tmser.blog.model.entity.Content;
import com.tmser.blog.repository.base.BaseRepository;
import org.apache.ibatis.annotations.Mapper;

/**
 * Base content repository.
 *
 * @author guqing
 * @date 2021-12-18
 */
@Mapper
public interface ContentRepository extends BaseRepository<Content> {

}
