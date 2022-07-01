package com.tmser.blog.repository;

import com.tmser.blog.model.entity.Log;
import com.tmser.blog.repository.base.BaseRepository;
import org.apache.ibatis.annotations.Mapper;

/**
 * Log repository.
 *
 * @author johnniang
 */
@Mapper
public interface LogRepository extends BaseRepository<Log> {
}
