package com.tmser.blog.repository;

import com.tmser.blog.model.entity.SheetComment;
import com.tmser.blog.model.enums.CommentStatus;
import com.tmser.blog.model.projection.CommentChildrenCountProjection;
import com.tmser.blog.model.projection.CommentCountProjection;
import com.tmser.blog.repository.base.BaseCommentRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.List;

/**
 * Sheet comment repository.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-04-24
 */
@Mapper
public interface SheetCommentRepository extends BaseCommentRepository<SheetComment> {

    /**
     * Count comments by sheet ids.
     *
     * @param sheetIds sheet id collection must not be null
     * @return a list of CommentCountProjection
     */
    @NonNull
    @Select({"<script>"," select count(comment.id) count, comment.post_id postId from comments comment where type=1 and comment.post_id in",
            "<foreach item='item' index='index' collection='items' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach> group by comment.post_id </script>"})
    @Override
    List<CommentCountProjection> countByPostIds(@NonNull Collection<Integer> sheetIds);

    /**
     * Counts comment count by comment status and sheet id collection.
     *
     * @param status   status must not be null
     * @param sheetsId sheet id collection must not be null
     * @return a list of comment count
     */
    @NonNull
    @Select({"<script>"," select count(comment.id) count, comment.post_id postId from comments comment where type = 1 and comment.post_id in",
            "<foreach item='item' index='index' collection='items' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            " and comment.status = #{status} ",
            " group by comment.post_id",
            "</script>"})
    @Override
    List<CommentCountProjection> countByStatusAndPostIds(@NonNull CommentStatus status,
                                                         @NonNull Collection<Integer> sheetsId);

    /**
     * Finds direct children count by comment ids.
     *
     * @param commentIds comment ids must not be null.
     * @return a list of CommentChildrenCountProjection
     */
    @NonNull
    @Select({"<script>"," select count(comment.id) directChildrenCount, comment.parent_id commentId " +
            "from comments comment where type = 1 and comment.parent_id in",
            "<foreach item='item' index='index' collection='items' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            " and comment.status = #{status} ",
            " group by comment.parent_id",
            "</script>"})
    List<CommentChildrenCountProjection> findDirectChildrenCount(
            @NonNull Collection<Long> commentIds);
}
