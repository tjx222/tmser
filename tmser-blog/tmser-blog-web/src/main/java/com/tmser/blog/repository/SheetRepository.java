package com.tmser.blog.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tmser.blog.model.entity.Post;
import com.tmser.blog.model.entity.Sheet;
import com.tmser.blog.model.enums.PostStatus;
import com.tmser.blog.repository.base.BasePostRepository;
import com.tmser.model.sort.Sort;
import org.apache.ibatis.annotations.*;
import org.springframework.lang.NonNull;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Sheet repository.
 *
 * @author johnniang
 * @date 3/22/19
 */
@Mapper
public interface SheetRepository extends BasePostRepository<Sheet> {

    /**
     * Count all sheet visits.
     *
     * @return visits.
     */
    @Override
    @Select("select sum(p.visits) from posts p where p.type = " + Post.T_SHEET)
    Long countVisit();

    /**
     * Count all sheet likes.
     *
     * @return likes.
     */
    @Override
    @Select("select sum(p.likes) from posts p where p.type = " + Post.T_SHEET )
    Long countLike();

    /**
     * Gets sheet by slug and status.
     *
     * @param slug   slug must not be blank
     * @param status status must not be null
     * @return an optional of sheet.
     */
    @NonNull
    @Override
    @Select("select * from posts  where type = " + Post.T_SHEET + " and slug=#{slug} and status = #{status}")
    Optional<Sheet> getBySlugAndStatus(@NonNull String slug, @NonNull PostStatus status);

    /**
     * Get Post by slug
     *
     * @param slug Post slug
     * @return Post or empty
     */
    @Select("select * from posts where type = " + Post.T_SHEET + " and slug = #{slug} limit 1")
    Optional<Sheet> getBySlug(@NonNull String slug);

    /**
     * Determine if the slug exists.
     *
     * @param slug slug must not be null.
     * @return true or false.
     */
    @Select("select count(*) from posts where type = " + Post.T_SHEET + " and slug = #{slug} limit 1")
    boolean existsBySlug(@NonNull @Param("slug") String slug);

    /**
     * Determine if the slug exists.
     *
     * @param id   Post id must not be null.
     * @param slug slug must not be null.
     * @return true or false.
     */
    @Select("select count(*) from posts where type = " + Post.T_SHEET + " and slug = #{slug} and id <> #{id} limit 1")
    boolean existsByIdNotAndSlug(@NonNull @Param("id") Integer id, @Param("slug") @NonNull String slug);

    /**
     * Finds posts by status and pageable.
     *
     * @param status   Sheet status must not be null
     * @param pageable page info must not be null
     * @return a page of Sheet
     */
    @NonNull
    @Select("select * from posts where type = " + Post.T_SHEET + " and status = #{status}")
    IPage<Sheet> findPageByStatus(@NonNull PostStatus status, @NonNull IPage pageable);

    /**
     * Finds posts by status.
     *
     * @param status Sheet staus must not be null
     * @return a list of Sheet
     */
    @NonNull
    @Select("select * from posts where type = " + Post.T_SHEET + " and status = #{status}")
    List<Sheet> findAllByStatus(@NonNull PostStatus status);

    /**
     * Finds posts by status.
     *
     * @param status post staus must not be null
     * @param sort   sort info must not be null
     * @return a list of post
     */
    @NonNull
    @Select("select * from posts where type = " + Post.T_SHEET + " and status = #{status} order by #{sort}")
    List<Sheet> findAndSortAllByStatus(@NonNull @Param("status") PostStatus status, @Param("sort") @NonNull Sort sort);

    /**
     * Finds all Sheet by status and create time before.
     *
     * @param status     status must not be null
     * @param createTime create time must not be null
     * @param pageable   page info must not be null
     * @return a page of Sheet
     */
    @NonNull
    @Select("select * from posts where type = " + Post.T_SHEET + " and status = #{status}  and create_time < #{createTime}")
    IPage<Sheet> findAllByStatusAndCreateTimeBefore(@NonNull @Param("status") PostStatus status,
                                                    @NonNull @Param("createTime") Date createTime, @NonNull IPage pageable);

    /**
     * Finds all Sheet by status and create time after.
     *
     * @param status     status must not be null
     * @param createTime create time must not be null
     * @param pageable   page info must not be null
     * @return a page of Sheet
     */
    @NonNull
    @Select("select * from posts where type = " + Post.T_SHEET + " and status = #{status}  and create_time > #{createTime}")
    IPage<Sheet> findAllByStatusAndCreateTimeAfter(@NonNull @Param("status") PostStatus status,
                                                   @NonNull @Param("createTime") Date createTime, @NonNull IPage pageable);

    /**
     * Finds all Sheet by status and edit time before.
     *
     * @param status   status must not be null
     * @param editTime edit time must not be null
     * @param pageable page info must not be null
     * @return a page of Sheet
     */
    @NonNull
    @Select("select * from posts where type = " + Post.T_SHEET + " and status = #{status}  and edit_time < #{editTime}")
    IPage<Sheet> findAllByStatusAndEditTimeBefore(@NonNull @Param("status") PostStatus status, @NonNull @Param("editTime") Date editTime,
                                                  @NonNull IPage pageable);

    /**
     * Finds all Sheet by status and edit time after.
     *
     * @param status   status must not be null
     * @param editTime edit time must not be null
     * @param pageable page info must not be null
     * @return a page of Sheet
     */
    @NonNull
    @Select("select * from posts where type = " + Post.T_SHEET + " and status = #{status}  and edit_time > #{editTime}")
    IPage<Sheet> findAllByStatusAndEditTimeAfter(@NonNull @Param("status") PostStatus status, @NonNull @Param("editTime") Date editTime,
                                                 @NonNull IPage pageable);

    /**
     * Finds all Sheet by status and visits before.
     *
     * @param status   status must not be null
     * @param visits   visits must not be null
     * @param pageable page info must not be null
     * @return a page of Sheet
     */
    @NonNull
    @Select("select * from posts where type = " + Post.T_SHEET + " and status = #{status}  and edit_time > #{editTime}")
    IPage<Sheet> findAllByStatusAndVisitsBefore(@NonNull @Param("status") PostStatus status, @NonNull Long visits,
                                                @NonNull IPage pageable);

    /**
     * Finds all Sheet by status and visits after.
     *
     * @param status   status must not be null
     * @param visits   visits must not be null
     * @param pageable page info must not be null
     * @return a page of Sheet
     */
    @NonNull
    @Select("select * from posts where type = " + Post.T_SHEET + " and status = #{status}  and visits > #{visits}")
    IPage<Sheet> findAllByStatusAndVisitsAfter(@NonNull @Param("status") PostStatus status, @NonNull @Param("visits") Long visits,
                                               @NonNull IPage pageable);

    /**
     * Counts posts by status and type.
     *
     * @param status status
     * @return posts count
     */
    @Select("select count(*) from posts where type = " + Post.T_SHEET + " and status = #{status}")
    long countByStatus(@NonNull @Param("status") PostStatus status);
}
