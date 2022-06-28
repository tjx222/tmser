package com.tmser.blog.repository.base;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tmser.blog.model.entity.BasePost;
import com.tmser.blog.model.enums.PostStatus;
import com.tmser.model.sort.Sort;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.lang.NonNull;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Base post repository.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-03-22
 */
public interface BasePostRepository<POST extends BasePost> extends BaseRepository<POST> {

    /**
     * Counts visits. (Need to be overridden)
     *
     * @return total visits
     */
    @Select("select sum(p.visits) from BasePost p")
    Long countVisit();

    /**
     * Counts likes. (Need to be overridden)
     *
     * @return total likes
     */
    @Select("select sum(p.likes) from BasePost p")
    Long countLike();

    /**
     * Finds posts by status and pageable.
     *
     * @param status   post status must not be null
     * @param pageable page info must not be null
     * @return a page of post
     */
    @NonNull
    IPage<POST> findAllByStatus(@NonNull PostStatus status, @NonNull IPage pageable);

    /**
     * Finds posts by status.
     *
     * @param status post staus must not be null
     * @return a list of post
     */
    @NonNull
    List<POST> findAllByStatus(@NonNull PostStatus status);

    /**
     * Finds posts by status.
     *
     * @param status post staus must not be null
     * @param sort   sort info must not be null
     * @return a list of post
     */
    @NonNull
    List<POST> findAllByStatus(@NonNull PostStatus status, @NonNull Sort sort);

    /**
     * Finds all post by status and create time before.
     *
     * @param status     status must not be null
     * @param createTime create time must not be null
     * @param pageable   page info must not be null
     * @return a page of post
     */
    @NonNull
    IPage<POST> findAllByStatusAndCreateTimeBefore(@NonNull PostStatus status,
                                                   @NonNull Date createTime, @NonNull IPage pageable);

    /**
     * Finds all post by status and create time after.
     *
     * @param status     status must not be null
     * @param createTime create time must not be null
     * @param pageable   page info must not be null
     * @return a page of post
     */
    @NonNull
    IPage<POST> findAllByStatusAndCreateTimeAfter(@NonNull PostStatus status,
                                                  @NonNull Date createTime, @NonNull IPage pageable);

    /**
     * Finds all post by status and edit time before.
     *
     * @param status   status must not be null
     * @param editTime edit time must not be null
     * @param pageable page info must not be null
     * @return a page of post
     */
    @NonNull
    IPage<POST> findAllByStatusAndEditTimeBefore(@NonNull PostStatus status, @NonNull Date editTime,
                                                 @NonNull IPage pageable);

    /**
     * Finds all post by status and edit time after.
     *
     * @param status   status must not be null
     * @param editTime edit time must not be null
     * @param pageable page info must not be null
     * @return a page of post
     */
    @NonNull
    IPage<POST> findAllByStatusAndEditTimeAfter(@NonNull PostStatus status, @NonNull Date editTime,
                                                @NonNull IPage pageable);

    /**
     * Finds all post by status and visits before.
     *
     * @param status   status must not be null
     * @param visits   visits must not be null
     * @param pageable page info must not be null
     * @return a page of post
     */
    @NonNull
    IPage<POST> findAllByStatusAndVisitsBefore(@NonNull PostStatus status, @NonNull Long visits,
                                               @NonNull IPage pageable);

    /**
     * Finds all post by status and visits after.
     *
     * @param status   status must not be null
     * @param visits   visits must not be null
     * @param pageable page info must not be null
     * @return a page of post
     */
    @NonNull
    IPage<POST> findAllByStatusAndVisitsAfter(@NonNull PostStatus status, @NonNull Long visits,
                                              @NonNull IPage pageable);

    /**
     * Gets post by slug and status.
     *
     * @param slug   slug must not be blank
     * @param status status must not be null
     * @return an optional post
     */
    @NonNull
    Optional<POST> getBySlugAndStatus(@NonNull String slug, @NonNull PostStatus status);

    /**
     * Gets post by id and status.
     *
     * @param id     id must not be blank
     * @param status status must not be null
     * @return an optional post
     */
    @NonNull
    Optional<POST> getByIdAndStatus(@NonNull Integer id, @NonNull PostStatus status);


    /**
     * Counts posts by status and type.
     *
     * @param status status
     * @return posts count
     */
    long countByStatus(@NonNull PostStatus status);

    /**
     * Determine if the slug exists.
     *
     * @param slug slug must not be null.
     * @return true or false.
     */
    boolean existsBySlug(@NonNull String slug);

    /**
     * Determine if the slug exists.
     *
     * @param id   post id must not be null.
     * @param slug slug must not be null.
     * @return true or false.
     */
    boolean existsByIdNotAndSlug(@NonNull Integer id, @NonNull String slug);

    /**
     * Get post by slug
     *
     * @param slug post slug
     * @return post or empty
     */
    Optional<POST> getBySlug(@NonNull String slug);

    /**
     * Updates post visits.
     *
     * @param visits visit delta
     * @param postId post id must not be null
     * @return updated rows
     */
    @Select("update BasePost p set p.visits = p.visits + :visits where p.id = :postId")
    int updateVisit(@Param("visits") long visits, @Param("postId") @NonNull Integer postId);

    /**
     * Updates post likes.
     *
     * @param likes  likes delta
     * @param postId post id must not be null
     * @return updated rows
     */
    @Select("update BasePost p set p.likes = p.likes + :likes where p.id = :postId")
    int updateLikes(@Param("likes") long likes, @Param("postId") @NonNull Integer postId);

    /**
     * Updates post status by post id.
     *
     * @param status post status must not be null.
     * @param postId post id must not be null.
     * @return updated rows.
     */
    @Select("update BasePost p set p.status = :status where p.id = :postId")
    int updateStatus(@Param("status") @NonNull PostStatus status,
                     @Param("postId") @NonNull Integer postId);
}
