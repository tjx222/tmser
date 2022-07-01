package com.tmser.blog.repository;

import com.tmser.blog.model.entity.JournalComment;
import com.tmser.blog.model.enums.CommentStatus;
import com.tmser.blog.model.projection.CommentChildrenCountProjection;
import com.tmser.blog.model.projection.CommentCountProjection;
import com.tmser.blog.repository.base.BaseCommentRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.lang.NonNull;

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
    @Select({"<script>"," select count(comment.id) `count`, comment.post_id postId " +
            "from comments comment where comment.type = 2 and comment.post_id in ",
            "<foreach item='item' index='index' collection='items' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            " group by comment.post_id",
            " </script>"})
    List<CommentCountProjection> countByPostIds(@NonNull Collection<Integer> postIds);

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
            "<foreach item='item' index='index' collection='items' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            " group by comment.post_id",
            " </script>"})
    @Override
    List<CommentCountProjection> countByStatusAndPostIds(@NonNull@Param("status") CommentStatus status,
                                                         @NonNull Collection<Integer> journalsId);

    /**
     * Finds direct children count by comment ids.
     *
     * @param commentIds comment ids must not be null.
     * @return a list of CommentChildrenCountProjection
     */
    @NonNull
    @Select({"<script>"," select count(comment.id) directChildrenCount, comment.parent_id commentId " +
            "from comments comment where comment.type = 0 and comment.parent_id in ",
            "<foreach item='item' index='index' collection='items' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            " group by comment.parent_id",
            " </script>"})
    List<CommentChildrenCountProjection> findDirectChildrenCount(
            @NonNull Collection<Long> commentIds);
}
