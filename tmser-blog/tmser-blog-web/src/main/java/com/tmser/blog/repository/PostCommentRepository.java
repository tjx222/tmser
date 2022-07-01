package com.tmser.blog.repository;

import com.tmser.blog.model.entity.PostComment;
import com.tmser.blog.model.enums.CommentStatus;
import com.tmser.blog.model.projection.CommentChildrenCountProjection;
import com.tmser.blog.model.projection.CommentCountProjection;
import com.tmser.blog.repository.base.BaseCommentRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * PostComment repository.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-03-21
 */
@Mapper
public interface PostCommentRepository extends BaseCommentRepository<PostComment> {

    /**
     * Count comments by post ids.
     *
     * @param postIds post id collection must not be null
     * @return a list of CommentCountProjection
     */
    @NonNull
    @Override
    @Select({"<script>"," select count(comment.id) `count`, comment.post_id postId " +
            "from comments comment where comment.type = 0 and comment.post_id in ",
            "<foreach item='item' index='index' collection='items' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            " group by comment.post_id",
            " </script>"})
    List<CommentCountProjection> countByPostIds(@NonNull Collection<Integer> postIds);

    /**
     * Counts comment count by comment status and post id collection.
     *
     * @param status  status must not be null
     * @param postsId post id collection must not be null
     * @return a list of comment count
     */
    @NonNull
    @Select({"<script>"," select count(comment.id) `count`, comment.post_id postId " +
            "from comments comment where comment.type = 0 and comment.status = #{status} and comment.post_id in ",
            "<foreach item='item' index='index' collection='items' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            " group by comment.post_id",
            " </script>"})
    @Override
    List<CommentCountProjection> countByStatusAndPostIds(@NonNull@Param("status") CommentStatus status,
                                                         @NonNull Collection<Integer> postsId);

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

    /**
     * 根据时间范围和IP地址统计评论次数
     *
     * @param ipAddress IP地址
     * @param startTime 起始时间
     * @param endTime   结束时间
     * @return 评论次数
     */
    @Select("SELECT COUNT(id) FROM comments WHERE ip_address=#{ipAddress} AND update_time BETWEEN #{startTime} AND #{endTime}"
            + " AND status <> 2")
    int countByIpAndTime(@Param("ipAddress") String ipAddress,
                         @Param("startTime") Date startTime,
                         @Param("endTime") Date endTime);
}
