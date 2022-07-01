package com.tmser.blog.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tmser.blog.model.entity.Journal;
import com.tmser.blog.model.enums.JournalType;
import com.tmser.blog.repository.base.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.lang.NonNull;

/**
 * Journal repository.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-03-22
 */
@Mapper
public interface JournalRepository
        extends BaseRepository<Journal> {

    /**
     * Finds journals by type and pageable.
     *
     * @param type     journal type must not be null
     * @param pageable page info must not be null
     * @return a page of journal
     */
    @NonNull
    @Select("select * from journals where type = #{type}")
    IPage<Journal> findAllByType(@Param("type") @NonNull JournalType type, @NonNull IPage pageable);

    /**
     * Updates journal likes.
     *
     * @param likes likes delta
     * @param id    id must not be null
     * @return updated rows
     */
    @Select("update journals j set j.likes = j.likes + #{likes} where j.id = #{id}")
    int updateLikes(@Param("likes") long likes, @Param("id") @NonNull Integer id);
}
