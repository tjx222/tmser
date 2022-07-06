package com.tmser.blog.repository.base;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tmser.blog.annotation.SensitiveConceal;
import com.tmser.blog.model.entity.BaseComment;
import com.tmser.blog.model.enums.CommentStatus;
import com.tmser.blog.model.projection.CommentChildrenCountProjection;
import com.tmser.blog.model.projection.CommentCountProjection;
import com.tmser.model.sort.Sort;
import com.tmser.spring.web.PageableDefault;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Base comment repository.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-04-24
 */
public interface BaseCommentRepository<COMMENT extends BaseComment>
        extends BaseRepository<COMMENT> {

    /**
     * Finds all comments by status.
     *
     * @param status   status must not be null
     * @param pageable page info must not be null
     * @return a page of comment
     */
    @NonNull
    @SensitiveConceal
    IPage<COMMENT> findAllByStatus(@Nullable @Param("status") CommentStatus status, @NonNull IPage pageable);

    /**
     * Counts by comment status.
     *
     * @param status comment status must not be null
     * @return comment count
     */
    long countByStatus(@NonNull @Param("status") CommentStatus status);


    /**
     * Finds all comments by post id.
     *
     * @param postId post id must not be null
     * @return a list of comment
     */
    @NonNull
    @SensitiveConceal
    @Select("select * from comments where post_id = #{postId}")
    List<COMMENT> findAllByPostId(@NonNull @Param("postId") Serializable postId);

    /**
     * Counts comment count by post id collection.
     *
     * @param postIds post id collection must not be null
     * @return a list of comment count
     */
    @NonNull
    List<CommentCountProjection> countByPostIds(@NonNull Collection<Integer> postIds);

    /**
     * Counts comment count by comment status and post id collection.
     *
     * @param status  status must not be null
     * @param postIds post id collection must not be null
     * @return a list of comment count
     */
    @NonNull
    List<CommentCountProjection> countByStatusAndPostIds(@NonNull @Param("status") CommentStatus status,
                                                         @NonNull Collection<Integer> postIds);


    /**
     * 根据id 查询所有
     *
     * @param parentIds
     * @param sort
     */
    @Select({"<script>","select * from comments where id in ",
            "<foreach item='item' index='index' collection='parentIds' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            " order by #{sort}",
            "</script>"})
    List<COMMENT> findAllByIdIn(@Param("parentIds") Collection<Long> parentIds, @Param("sort") Sort sort);

    /**
     * Count comments by post id.
     *
     * @param postId post id must not be null.
     * @return comments count
     */
    @Select("select count(*) from comments where post_id = #{postId}")
    long countByPostId(@NonNull @Param("postId") Integer postId);

    /**
     * Count comments by comment status and post id.
     *
     * @param status status must not be null
     * @param postId post id must not be null.
     * @return comments count
     */
    @Select("select count(*) from comments where status = #{status} and post_id = #{postId}")
    long countByStatusAndPostId(@NonNull @Param("status") CommentStatus status, @NonNull @Param("postId") Integer postId);


    /**
     * Removes comments by post id.
     *
     * @param postId post id must not be null
     * @return a list of comment deleted
     */
    @NonNull
    @Update("delete from comments where post_id = #{postId}")
    void deleteByPostId(@NonNull @Param("postId") Integer postId);

    /**
     * Finds comments by post id, comment status.
     *
     * @param postId post id must not be null
     * @param status comment status must not be null
     * @return a list of comment
     */
    @NonNull
    @SensitiveConceal
    @Select("select * from comments where status = #{status} and post_id = #{postId}")
    List<COMMENT> findAllByPostIdAndStatus(@Param("postId") Integer postId, @Param("status") CommentStatus status);

    /**
     * Finds comments by post id and comment status.
     *
     * @param postId   post id must not be null
     * @param status   comment status must not be null
     * @param pageable page info must not be null
     * @return a page of comment
     */
    @NonNull
    @SensitiveConceal
    @Select("select * from comments where status = #{status} and post_id = #{postId}")
    IPage<COMMENT> findPageByPostIdAndStatus(@Param("postId") Integer postId, @Param("status") CommentStatus status, IPage pageable);

    /**
     * Finds comments by post id, comment status and parent id.
     *
     * @param postId   post id must not be null
     * @param status   comment status must not be null
     * @param parentId comment parent id must not be null
     * @return a list of comment
     */
    @NonNull
    @SensitiveConceal
    @Select("select * from comments where status = #{status} and post_id = #{postId} and parent_id = #{parentId}")
    List<COMMENT> findAllByPostIdAndStatusAndParentId(@NonNull @Param("postId") Integer postId,
                                                      @NonNull @Param("status") CommentStatus status,
                                                      @NonNull @Param("parentId") Long parentId);

    /**
     * Finds comments by post id, comment status and parent id.
     *
     * @param postId   post id must not be null
     * @param status   comment status must not be null
     * @param parentId comment parent id must not be null
     * @param pageable page info must not be null
     * @return a page of comment
     */
    @NonNull
    @SensitiveConceal
    @Select("select * from comments where status = #{status} and post_id = #{postId} and parent_id = #{parentId}")
    IPage<COMMENT> findPageByPostIdAndStatusAndParentId(@NonNull @Param("postId") Integer postId,
                                                       @NonNull @Param("status") CommentStatus status,
                                                       @NonNull @Param("parentId") Long parentId, IPage pageable);

    /**
     * Finds comments by post id and parent id.
     *
     * @param postId   post id must not be null
     * @param parentId comment parent id must not be null
     * @return a list of comment
     */
    @NonNull
    @SensitiveConceal
    @Select("select * from comments where post_id = #{postId} and parent_id = #{parentId}")
    List<COMMENT> findAllByPostIdAndParentId(@NonNull @Param("postId") Integer postId,
                                             @NonNull @Param("parentId") Long parentId);

    /**
     * Finds all comments by status and parent id collection.
     *
     * @param status    comment status must not be null
     * @param parentIds parent id collection must not be null
     * @return a list of comment
     */
    @NonNull
    @SensitiveConceal
    @Select({"<script>","select * from comments where status = #{status} and parent_id in ",
            "<foreach item='item' index='index' collection='parentIds' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"})
    List<COMMENT> findAllByStatusAndParentIdIn(@NonNull @Param("status") CommentStatus status,
                                               @NonNull @Param("parentIds") Collection<Long> parentIds);

    /**
     * Finds all comments by parent id collection.
     *
     * @param parentIds parent id collection must not be null
     * @return a list of comment
     */
    @SensitiveConceal
    @Select({"<script>","select * from comments where parent_id in ",
            "<foreach item='item' index='index' collection='parentIds' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"})
    List<COMMENT> findAllByParentIdIn(@NonNull @Param("parentIds") Collection<Long> parentIds);


    /**
     * Finds direct children count by comment ids and status.
     *
     * @param commentIds comment ids must not be null.
     * @param status     comment status must not be null.
     * @return a list of CommentChildrenCountProjection
     */
    @NonNull
    List<CommentChildrenCountProjection> findDirectChildrenCount(
            @NonNull Collection<Long> commentIds, @NonNull @Param("status") CommentStatus status);
}
