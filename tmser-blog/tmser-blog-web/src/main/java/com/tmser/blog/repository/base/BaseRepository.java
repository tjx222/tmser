package com.tmser.blog.repository.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tmser.blog.model.entity.SheetMeta;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Set;


/**
 * Base repository interface contains some common methods.
 *
 * @param <D> domain type
 * @author johnniang
 * @author ryanwang
 * @date 2019-03-15
 */
public interface BaseRepository<D> extends BaseMapper<D> {

}
