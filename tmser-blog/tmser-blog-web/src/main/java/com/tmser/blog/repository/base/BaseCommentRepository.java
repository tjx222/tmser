package com.tmser.blog.repository.base;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tmser.blog.annotation.SensitiveConceal;
import com.tmser.blog.model.entity.BaseComment;
import com.tmser.blog.model.enums.CommentStatus;
import com.tmser.blog.model.projection.CommentChildrenCountProjection;
import com.tmser.blog.model.projection.CommentCountProjection;
import com.tmser.model.sort.Sort;
import org.apache.ibatis.annotations.Select;
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
    IPage<COMMENT> findAllByStatus(@Nullable CommentStatus status, @NonNull IPage pageable);


    /**
     * Finds all comments by post ids.
     *
     * @param postIds post ids must not be null
     * @return a list of comment
     */
    @NonNull
    @SensitiveConceal
    List<COMMENT> findAllByPostIdIn(@NonNull Collection<Integer> postIds);

    /**
     * Finds all comments by post id.
     *
     * @param postId post id must not be null
     * @return a list of comment
     */
    @NonNull
    @SensitiveConceal
    List<COMMENT> findAllByPostId(@NonNull Serializable postId);

    /**
     * Counts comment count by post id collection.
     *
     * @param postIds post id collection must not be null
     * @return a list of comment count
     */
    @Select(
            "select new com.tmser.blog.model.projection.CommentCountProjection(count(comment.id), "
                    + "comment.postId) "
                    + "from BaseComment comment "
                    + "where comment.postId in ?1 "
                    + "group by comment.postId")
    @NonNull
    List<CommentCountProjection> countByPostIds(@NonNull Collection<Integer> postIds);

    /**
     * Counts comment count by comment status and post id collection.
     *
     * @param status  status must not be null
     * @param postIds post id collection must not be null
     * @return a list of comment count
     */
    @Select(
            "select new com.tmser.blog.model.projection.CommentCountProjection(count(comment.id), "
                    + "comment.postId) "
                    + "from BaseComment comment "
                    + "where comment.status = ?1 "
                    + "and comment.postId in ?2 "
                    + "group by comment.postId")
    @NonNull
    List<CommentCountProjection> countByStatusAndPostIds(@NonNull CommentStatus status,
                                                         @NonNull Collection<Integer> postIds);


    /**
     * 根据id 查询所有
     *
     * @param parentIds
     * @param sort
     */
    List<COMMENT> findAllByIdIn(Collection<Long> parentIds, Sort sort);

    /**
     * Count comments by post id.
     *
     * @param postId post id must not be null.
     * @return comments count
     */
    long countByPostId(@NonNull Integer postId);

    /**
     * Count comments by comment status and post id.
     *
     * @param status status must not be null
     * @param postId post id must not be null.
     * @return comments count
     */
    long countByStatusAndPostId(@NonNull CommentStatus status, @NonNull Integer postId);

    /**
     * Counts by comment status.
     *
     * @param status comment status must not be null
     * @return comment count
     */
    long countByStatus(@NonNull CommentStatus status);

    /**
     * Removes comments by post id.
     *
     * @param postId post id must not be null
     * @return a list of comment deleted
     */
    @NonNull
    List<COMMENT> deleteByPostId(@NonNull Integer postId);

    /**
     * Removes comments by parent id.
     *
     * @param id comment id must not be null
     * @return a list of comment deleted
     */
    @NonNull
    List<COMMENT> deleteByParentId(@NonNull Long id);

    /**
     * Finds comments by post id, comment status.
     *
     * @param postId post id must not be null
     * @param status comment status must not be null
     * @return a list of comment
     */
    @NonNull
    @SensitiveConceal
    List<COMMENT> findAllByPostIdAndStatus(Integer postId, CommentStatus status);

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
    IPage<COMMENT> findAllByPostIdAndStatus(Integer postId, CommentStatus status, IPage pageable);

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
    List<COMMENT> findAllByPostIdAndStatusAndParentId(@NonNull Integer postId,
                                                      @NonNull CommentStatus status, @NonNull Long parentId);

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
    IPage<COMMENT> findAllByPostIdAndStatusAndParentId(Integer postId, CommentStatus status,
                                                       Long parentId, IPage pageable);

    /**
     * Finds comments by post id and parent id.
     *
     * @param postId   post id must not be null
     * @param parentId comment parent id must not be null
     * @return a list of comment
     */
    @NonNull
    @SensitiveConceal
    List<COMMENT> findAllByPostIdAndParentId(@NonNull Integer postId, @NonNull Long parentId);

    /**
     * Finds all comments by status and parent id collection.
     *
     * @param status    comment status must not be null
     * @param parentIds parent id collection must not be null
     * @return a list of comment
     */
    @NonNull
    @SensitiveConceal
    List<COMMENT> findAllByStatusAndParentIdIn(@NonNull CommentStatus status,
                                               @NonNull Collection<Long> parentIds);

    /**
     * Finds all comments by parent id collection.
     *
     * @param parentIds parent id collection must not be null
     * @return a list of comment
     */
    @SensitiveConceal
    List<COMMENT> findAllByParentIdIn(@NonNull Collection<Long> parentIds);


    /**
     * Finds direct children count by comment ids and status.
     *
     * @param commentIds comment ids must not be null.
     * @param status     comment status must not be null.
     * @return a list of CommentChildrenCountProjection
     */
    @Select(
            "select new com.tmser.blog.model.projection.CommentChildrenCountProjection(count(comment"
                    + ".id), comment.parentId) "
                    + "from BaseComment comment "
                    + "where comment.parentId in ?1 "
                    + "and comment.status = ?2 "
                    + "group by comment.parentId")
    @NonNull
    List<CommentChildrenCountProjection> findDirectChildrenCount(
            @NonNull Collection<Long> commentIds, @NonNull CommentStatus status);
}
