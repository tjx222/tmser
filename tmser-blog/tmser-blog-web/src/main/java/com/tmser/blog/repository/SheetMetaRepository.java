package com.tmser.blog.repository;

import com.tmser.blog.model.entity.SheetMeta;
import com.tmser.blog.repository.base.BaseMetaRepository;
import com.tmser.blog.repository.base.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Set;

/**
 * SheetMeta repository.
 *
 * @author ryanwang
 * @author ikaisec
 * @date 2019-08-04
 */
@Mapper
public interface SheetMetaRepository extends BaseMetaRepository<SheetMeta> {
    /**
     * Finds all metas by post id.
     *
     * @param postId post id must not be null
     * @return a list of meta
     */
    @NonNull
    @Select("select id, post_id, type, meta_key as `key`, meta_value as `value` from metas where type = 1 and post_id= #{postId}")
    List<SheetMeta> findAllByPostId(@NonNull Integer postId);

    /**
     * Deletes post metas by post id.
     *
     * @param postId post id must not be null
     * @return a list of post meta deleted
     */
    @NonNull
    @Update("delete from metas where type = 1 and post_id= #{postId}")
    void deleteByPostId(@NonNull Integer postId);

    /**
     * Finds all post metas by post id.
     *
     * @param postIds post id must not be null
     * @return a list of post meta
     */
    @NonNull
    @Select({"<script>"," select id, post_id, type, meta_key as `key`, meta_value as `value` from metas where type = 1 and post_id in ",
            "<foreach item='item' index='index' collection='postIds' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach></script>"})
    List<SheetMeta> findAllByPostIdIn(@NonNull @Param("postIds") Set<Integer> postIds);
}
