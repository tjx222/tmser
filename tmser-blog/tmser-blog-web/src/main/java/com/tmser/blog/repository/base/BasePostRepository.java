package com.tmser.blog.repository.base;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tmser.blog.model.entity.BasePost;
import com.tmser.blog.model.enums.PostStatus;
import com.tmser.model.sort.Sort;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
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
    @Select("select sum(p.visits) from posts p")
    Long countVisit();

    /**
     * Counts likes. (Need to be overridden)
     *
     * @return total likes
     */
    @Select("select sum(p.likes) from posts p")
    Long countLike();

    /**
     * Finds posts by status and pageable.
     *
     * @param status   post status must not be null
     * @param pageable page info must not be null
     * @return a page of post
     */
    @NonNull
    @Select("select * from posts where status = #{status}")
    IPage<POST> findAllByStatus(@NonNull PostStatus status, @NonNull IPage pageable);

    /**
     * Finds posts by status.
     *
     * @param status post staus must not be null
     * @return a list of post
     */
    @NonNull
    @Select("select * from posts where status = #{status}")
    List<POST> findAllByStatus(@NonNull PostStatus status);

    /**
     * Finds posts by status.
     *
     * @param status post staus must not be null
     * @param sort   sort info must not be null
     * @return a list of post
     */
    @NonNull
    @Select("select * from posts where status = #{status} order by #{sort}" )
    List<POST> findAllByStatus(@NonNull @Param("status") PostStatus status, @Param("sort") @NonNull Sort sort);

    /**
     * Finds all post by status and create time before.
     *
     * @param status     status must not be null
     * @param createTime create time must not be null
     * @param pageable   page info must not be null
     * @return a page of post
     */
    @NonNull
    @Select("select * from posts where status = #{status}  and create_time < #{createTime}" )
    IPage<POST> findAllByStatusAndCreateTimeBefore(@NonNull @Param("status") PostStatus status,
                                                   @NonNull @Param("createTime") Date createTime, @NonNull IPage pageable);

    /**
     * Finds all post by status and create time after.
     *
     * @param status     status must not be null
     * @param createTime create time must not be null
     * @param pageable   page info must not be null
     * @return a page of post
     */
    @NonNull
    @Select("select * from posts where status = #{status}  and create_time > #{createTime}" )
    IPage<POST> findAllByStatusAndCreateTimeAfter(@NonNull @Param("status") PostStatus status,
                                                  @NonNull @Param("createTime")Date createTime, @NonNull IPage pageable);

    /**
     * Finds all post by status and edit time before.
     *
     * @param status   status must not be null
     * @param editTime edit time must not be null
     * @param pageable page info must not be null
     * @return a page of post
     */
    @NonNull
    @Select("select * from posts where status = #{status}  and edit_time < #{editTime}" )
    IPage<POST> findAllByStatusAndEditTimeBefore(@NonNull @Param("status") PostStatus status, @NonNull@Param("editTime") Date editTime,
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
    @Select("select * from posts where status = #{status}  and edit_time > #{editTime}" )
    IPage<POST> findAllByStatusAndEditTimeAfter(@NonNull @Param("status") PostStatus status, @NonNull@Param("editTime") Date editTime,
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
    @Select("select * from posts where status = #{status}  and edit_time > #{editTime}" )
    IPage<POST> findAllByStatusAndVisitsBefore(@NonNull @Param("status") PostStatus status, @NonNull Long visits,
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
    @Select("select * from posts where status = #{status}  and visits > #{visits}" )
    IPage<POST> findAllByStatusAndVisitsAfter(@NonNull @Param("status") PostStatus status, @NonNull @Param("visits") Long visits,
                                              @NonNull IPage pageable);

    /**
     * Gets post by slug and status.
     *
     * @param slug   slug must not be blank
     * @param status status must not be null
     * @return an optional post
     */
    @NonNull
    @Select("select * from posts where status = #{status}  and slug = #{slug}" )
    Optional<POST> getBySlugAndStatus(@NonNull @Param("slug") String slug, @NonNull @Param("status") PostStatus status);

    /**
     * Gets post by id and status.
     *
     * @param id     id must not be blank
     * @param status status must not be null
     * @return an optional post
     */
    @NonNull
    @Select("select * from posts where status = #{status}  and id = #{id}" )
    Optional<POST> getByIdAndStatus(@NonNull @Param("id") Integer id, @NonNull @Param("status") PostStatus status);


    /**
     * Counts posts by status and type.
     *
     * @param status status
     * @return posts count
     */
    @Select("select count(*) from posts where status = #{status}" )
    long countByStatus(@NonNull @Param("status") PostStatus status);

    /**
     * Determine if the slug exists.
     *
     * @param slug slug must not be null.
     * @return true or false.
     */
    @Select("select 1 from posts where slug = #{slug} limit 1" )
    boolean existsBySlug(@NonNull @Param("slug") String slug);

    /**
     * Determine if the slug exists.
     *
     * @param id   post id must not be null.
     * @param slug slug must not be null.
     * @return true or false.
     */
    @Select("select 1 from posts where slug = #{slug} and id = #{id} limit 1" )
    boolean existsByIdNotAndSlug(@NonNull @Param("id") Integer id, @Param("slug") @NonNull String slug);

    /**
     * Get post by slug
     *
     * @param slug post slug
     * @return post or empty
     */
    @Select("select * from posts where slug = #{slug} limit 1" )
    Optional<POST> getBySlug(@NonNull String slug);

    /**
     * Updates post visits.
     *
     * @param visits visit delta
     * @param postId post id must not be null
     * @return updated rows
     */
    @Update("update posts p set p.visits = p.visits + #{visits} where p.id = #{postId}")
    int updateVisit(@Param("visits") long visits, @Param("postId") @NonNull Integer postId);

    /**
     * Updates post likes.
     *
     * @param likes  likes delta
     * @param postId post id must not be null
     * @return updated rows
     */
    @Update("update posts p set p.likes = p.likes + #{likes} where p.id = #{postId}")
    int updateLikes(@Param("likes") long likes, @Param("postId") @NonNull Integer postId);

    /**
     * Updates post status by post id.
     *
     * @param status post status must not be null.
     * @param postId post id must not be null.
     * @return updated rows.
     */
    @Update("update posts p set p.status = #{status} where p.id = #{postId}")
    int updateStatus(@Param("status") @NonNull PostStatus status,
                     @Param("postId") @NonNull Integer postId);
}
