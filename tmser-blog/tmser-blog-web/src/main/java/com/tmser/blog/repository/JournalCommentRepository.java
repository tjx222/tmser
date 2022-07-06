package com.tmser.blog.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tmser.blog.annotation.SensitiveConceal;
import com.tmser.blog.model.entity.JournalComment;
import com.tmser.blog.model.entity.PostComment;
import com.tmser.blog.model.enums.CommentStatus;
import com.tmser.blog.model.projection.CommentChildrenCountProjection;
import com.tmser.blog.model.projection.CommentCountProjection;
import com.tmser.blog.repository.base.BaseCommentRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * Journal comment repository.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-04-24
 */
@Mapper
public interface JournalCommentRepository extends BaseCommentRepository<JournalComment> {

    /**
     * Count the number of comments by post id.
     *
     * @param postIds post id collection must not be null
     * @return a list of CommentCountProjection
     */
    @NonNull
    @Override
    @Select({"<script>","select count(comment.id) `count`, comment.post_id postId " +
            "from comments comment where comment.type = 2 and comment.post_id in ",
            "<foreach item='item' index='index' collection='postIds' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            " group by comment.post_id",
            " </script>"})
    List<CommentCountProjection> countByPostIds(@NonNull @Param("postIds") Collection<Integer> postIds);

    /**
     * Counts comment count by comment status and journal id collection.
     *
     * @param status     status must not be null
     * @param journalsId journal id collection must not be null
     * @return a list of comment count
     */
    @NonNull
    @Select({"<script>"," select count(comment.id) `count`, comment.post_id postId " +
            "from comments comment where comment.type = 2 and comment.status = #{status} and comment.post_id in ",
            "<foreach item='item' index='index' collection='journalsId' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            " group by comment.post_id",
            " </script>"})
    @Override
    List<CommentCountProjection> countByStatusAndPostIds(@NonNull@Param("status") CommentStatus status,
                                                         @NonNull @Param("journalsId") Collection<Integer> journalsId);

    /**
     * Finds direct children count by comment ids.
     *
     * @param commentIds comment ids must not be null.
     * @return a list of CommentChildrenCountProjection
     */
    @NonNull
    @Select({"<script>"," select count(comment.id) directChildrenCount, comment.parent_id commentId " +
            "from comments comment where comment.type = 2 and comment.parent_id in ",
            "<foreach item='item' index='index' collection='commentIds' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            " group by comment.parent_id",
            " </script>"})
    List<CommentChildrenCountProjection> findDirectChildrenCount(
            @NonNull @Param("commentIds") Collection<Long> commentIds);

    /**
     * Finds all comments by status.
     *
     * @param status   status must not be null
     * @param pageable page info must not be null
     * @return a page of comment
     */
    @NonNull
    @SensitiveConceal
    @Select("select * from comments where type = 2 and status = #{status}")
    IPage<JournalComment> findAllByStatus(@Nullable @Param("status") CommentStatus status, @NonNull IPage pageable);

    /**
     * Counts by comment status.
     *
     * @param status comment status must not be null
     * @return comment count
     */
    @Select("select count(*) from comments where type = 2 and status = #{status}")
    long countByStatus(@NonNull @Param("status") CommentStatus status);
}
