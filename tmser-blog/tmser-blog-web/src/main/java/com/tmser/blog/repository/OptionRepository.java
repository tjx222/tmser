package com.tmser.blog.repository;

import com.tmser.blog.model.entity.Option;
import com.tmser.blog.repository.base.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Optional;

/**
 * Option repository.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-03-20
 */
@Mapper
public interface OptionRepository
        extends BaseRepository<Option> {

    /**
     * Query option by key
     *
     * @param key key
     * @return Option
     */
    @Select("select * from options where option_key = #{key}")
    Optional<Option> findByKey(String key);

    /**
     * Delete option by key
     *
     * @param key key
     */
    @Update("delete from options where option_key = #{key}")
    void deleteByKey(String key);
}
