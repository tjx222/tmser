package com.tmser.blog.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tmser.blog.model.entity.Post;
import com.tmser.blog.model.entity.Post;
import com.tmser.blog.model.enums.PostStatus;
import com.tmser.blog.repository.base.BasePostRepository;
import com.tmser.model.sort.Sort;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;


/**
 * Post repository.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-03-19
 */
@Mapper
public interface PostRepository extends BasePostRepository<Post> {

    /**
     * Count all post visits.
     *
     * @return visits.
     */
    @Override
    @Select("select sum(p.visits) from posts p where p.type = " + Post.T_POST)
    Long countVisit();

    /**
     * Count all post likes.
     *
     * @return likes.
     */
    @Override
    @Select("select sum(p.likes) from posts p where p.type = "+ Post.T_POST)
    Long countLike();


    /**
     * Finds posts by status and pageable.
     *
     * @param status   post status must not be null
     * @param pageable page info must not be null
     * @return a page of post
     */
    @NonNull
    @Select("select * from posts where type = 0 and status = #{status}")
    IPage<Post> findPageByStatus(@NonNull PostStatus status, @NonNull IPage pageable);

    /**
     * Finds posts by status.
     *
     * @param status post staus must not be null
     * @return a list of post
     */
    @NonNull
    @Select("select * from posts where type = "+ Post.T_POST + " and status = #{status}")
    List<Post> findAllByStatus(@NonNull PostStatus status);

    /**
     * Find by post year and month and day and slug and status.
     *
     * @param year   post create year
     * @param month  post create month
     * @param day    post create day
     * @param slug   post slug
     * @param status post status
     * @return a optional of post
     */
    @Select({"<script>","select * from posts where type = "+ Post.T_POST,
            "<if test=\"year != null\">",
            " and year(post.create_time) = #{year}",
            "</if>",
            "<if test=\"month != null\">",
            " and month(post.create_time) = #{month}",
            "</if>",
            "<if test=\"day != null\">",
            " and day(post.create_time) = #{day}",
            "</if>",
            "<if test=\"slug != null\">",
            " and post.slug = #{slug}",
            "</if>",
            "<if test=\"status != null\">",
            " and post.status = #{status}",
            "</if>",
            "</script>"})
    Optional<Post> findBy(@Param("year") Integer year, @Param("month") Integer month,
                          @Param("day") Integer day, @Param("slug") String slug, @Param("status") PostStatus status);


    @Select({"<script>"," select * from posts where type = "+ Post.T_POST +  " and id in ",
            "<foreach item='item' index='index' collection='postIds' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach></script>"})
    IPage<Post> findAllByIdIn(@Param("postIds") Collection<Integer> postIds, IPage page);

    /**
     * Finds posts by status.
     *
     * @param status post staus must not be null
     * @param sort   sort info must not be null
     * @return a list of post
     */
    @NonNull
    @Select("select * from posts where type = "+ Post.T_POST + "  and status = #{status} order by #{sort}" )
    List<Post> findAndSortAllByStatus(@NonNull @Param("status") PostStatus status, @Param("sort") @NonNull Sort sort);

    /**
     * Get post by slug
     *
     * @param slug post slug
     * @return post or empty
     */
    @Select("select * from posts where type = "+ Post.T_POST + "  and slug = #{slug} limit 1" )
    Optional<Post> getBySlug(@NonNull String slug);

    /**
     * Determine if the slug exists.
     *
     * @param id   post id must not be null.
     * @param slug slug must not be null.
     * @return true or false.
     */
    @Select("select count(*) from posts where type = "+ Post.T_POST + "  and slug = #{slug} and id <> #{id} limit 1" )
    boolean existsByIdNotAndSlug(@NonNull @Param("id") Integer id, @Param("slug") @NonNull String slug);

    /**
     * Determine if the slug exists.
     *
     * @param slug slug must not be null.
     * @return true or false.
     */
    @Select("select count(*) from posts where type = "+ Post.T_POST + "  and slug = #{slug} limit 1" )
    boolean existsBySlug(@NonNull @Param("slug") String slug);

    /**
     * Finds all Post by status and create time before.
     *
     * @param status     status must not be null
     * @param createTime create time must not be null
     * @param pageable   page info must not be null
     * @return a page of Post
     */
    @NonNull
    @Select("select * from posts where type = "+ Post.T_POST + "  and status = #{status}  and create_time < #{createTime}" )
    IPage<Post> findAllByStatusAndCreateTimeBefore(@NonNull @Param("status") PostStatus status,
                                                    @NonNull @Param("createTime") Date createTime, @NonNull IPage pageable);

    /**
     * Finds all Post by status and create time after.
     *
     * @param status     status must not be null
     * @param createTime create time must not be null
     * @param pageable   page info must not be null
     * @return a page of Post
     */
    @NonNull
    @Select("select * from posts where type = "+ Post.T_POST + "  and status = #{status}  and create_time > #{createTime}" )
    IPage<Post> findAllByStatusAndCreateTimeAfter(@NonNull @Param("status") PostStatus status,
                                                   @NonNull @Param("createTime")Date createTime, @NonNull IPage pageable);

    /**
     * Finds all Post by status and edit time before.
     *
     * @param status   status must not be null
     * @param editTime edit time must not be null
     * @param pageable page info must not be null
     * @return a page of Post
     */
    @NonNull
    @Select("select * from posts where type = "+ Post.T_POST + "  and status = #{status}  and edit_time < #{editTime}" )
    IPage<Post> findAllByStatusAndEditTimeBefore(@NonNull @Param("status") PostStatus status, @NonNull@Param("editTime") Date editTime,
                                                  @NonNull IPage pageable);

    /**
     * Finds all Post by status and edit time after.
     *
     * @param status   status must not be null
     * @param editTime edit time must not be null
     * @param pageable page info must not be null
     * @return a page of Post
     */
    @NonNull
    @Select("select * from posts where type = "+ Post.T_POST + "  and status = #{status}  and edit_time > #{editTime}" )
    IPage<Post> findAllByStatusAndEditTimeAfter(@NonNull @Param("status") PostStatus status, @NonNull@Param("editTime") Date editTime,
                                                 @NonNull IPage pageable);

    /**
     * Finds all Post by status and visits before.
     *
     * @param status   status must not be null
     * @param visits   visits must not be null
     * @param pageable page info must not be null
     * @return a page of Post
     */
    @NonNull
    @Select("select * from posts where type = "+ Post.T_POST + "  and status = #{status}  and edit_time > #{editTime}" )
    IPage<Post> findAllByStatusAndVisitsBefore(@NonNull @Param("status") PostStatus status, @NonNull Long visits,
                                                @NonNull IPage pageable);

    /**
     * Finds all Post by status and visits after.
     *
     * @param status   status must not be null
     * @param visits   visits must not be null
     * @param pageable page info must not be null
     * @return a page of Post
     */
    @NonNull
    @Select("select * from posts where type = "+ Post.T_POST + "  and status = #{status}  and visits > #{visits}" )
    IPage<Post> findAllByStatusAndVisitsAfter(@NonNull @Param("status") PostStatus status, @NonNull @Param("visits") Long visits,
                                               @NonNull IPage pageable);

    /**
     * Counts posts by status and type.
     *
     * @param status status
     * @return posts count
     */
    @Select("select count(*) from posts where type = "+ Post.T_POST + "  and status = #{status}" )
    long countByStatus(@NonNull @Param("status") PostStatus status);
}
