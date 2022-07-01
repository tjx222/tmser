package com.tmser.blog.repository;

import com.tmser.blog.model.entity.ContentPatchLog;
import com.tmser.blog.model.enums.PostStatus;
import com.tmser.blog.repository.base.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Content patch log repository.
 *
 * @author guqing
 * @since 2022-01-04
 */
@Mapper
public interface ContentPatchLogRepository extends BaseRepository<ContentPatchLog> {

    /**
     * Finds the latest version by post id and post status.
     *
     * @param postId post id
     * @param status record status to Select
     * @return a {@link ContentPatchLog} record
     */
    @Select("select * from content_patch_logs where post_id = #{postId} and status = #{status} order by version desc limit 1")
    ContentPatchLog findFirstByPostIdAndStatusOrderByVersionDesc(@Param("postId") Integer postId, @Param("status") PostStatus status);

    /**
     * Finds the latest version by post id.
     *
     * @param postId post id to Select
     * @return a {@link ContentPatchLog} record of the latest version queried bby post id
     */
    @Select("select * from content_patch_logs where post_id = #{postId} order by version desc limit 1")
    ContentPatchLog findFirstByPostIdOrderByVersionDesc(Integer postId);

    /**
     * Finds all records below the specified version number by post id and status.
     *
     * @param postId  post id
     * @param version version number
     * @param status  record status
     * @return records below the specified version
     */
    @Select("select * from content_patch_logs c where c.post_id = #{postId} and c.version <= #{version} and c"
            + ".status=#{status} order by c.version desc")
    List<ContentPatchLog> findByPostIdAndStatusAndVersionLessThan(@Param("postId") Integer postId,
                                                                  @Param("version") Integer version,
                                                                  @Param("status") PostStatus status);

    /**
     * Finds by post id and version
     *
     * @param postId  post id
     * @param version version number
     * @return a {@link ContentPatchLog} record queried by post id and version
     */
    @Select("select * from content_patch_logs where post_id = #{postId} and version = #{version}")
    ContentPatchLog findByPostIdAndVersion(@Param("postId") Integer postId, @Param("version") Integer version);

    /**
     * Finds all records by post id and status and based on version number descending order
     *
     * @param postId post id
     * @param status status
     * @return a list of {@link ContentPatchLog} queried by post id and status
     */
    @Select("select * from content_patch_logs where post_id = #{postId} and status = #{status} order by version desc")
    List<ContentPatchLog> findAllByPostIdAndStatusOrderByVersionDesc(@Param("postId") Integer postId,
                                                                     @Param("status") PostStatus status);

    /**
     * Finds all records by post id.
     *
     * @param postId post id to Select
     * @return a list of {@link ContentPatchLog} queried by post id
     */
    @Select("select * from content_patch_logs where post_id = #{postId}")
    List<ContentPatchLog> findAllByPostId(Integer postId);
}
