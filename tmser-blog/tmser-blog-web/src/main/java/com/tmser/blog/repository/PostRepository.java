package com.tmser.blog.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tmser.blog.model.entity.Post;
import com.tmser.blog.model.enums.PostStatus;
import com.tmser.blog.repository.base.BasePostRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
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
    @Select("select sum(p.visits) from Post p")
    Long countVisit();

    /**
     * Count all post likes.
     *
     * @return likes.
     */
    @Override
    @Select("select sum(p.likes) from Post p")
    Long countLike();

    /**
     * Find by post year and month and slug.
     *
     * @param year  post create year
     * @param month post create month
     * @param slug  post slug
     * @return a optional of post
     */
    @Select("select post from Post post where year(post.createTime) = :year and month(post"
            + ".createTime) = :month and post.slug = :slug")
    Optional<Post> findBy(@Param("year") Integer year, @Param("month") Integer month,
                          @Param("slug") String slug);

    /**
     * Find by post year and slug.
     *
     * @param year post create year
     * @param slug post slug
     * @return a optional of post
     */
    @Select("select post from Post post where year(post.createTime) = :year and post.slug = :slug")
    Optional<Post> findBy(@Param("year") Integer year, @Param("slug") String slug);


    /**
     * Find by post year and month and slug and status.
     *
     * @param year   post create year
     * @param month  post create month
     * @param slug   post slug
     * @param status post status
     * @return a optional of post
     */
    @Select("select post from Post post where year(post.createTime) = :year and month(post"
            + ".createTime) = :month and post.slug = :slug and post.status = :status")
    Optional<Post> findBy(@Param("year") Integer year, @Param("month") Integer month,
                          @Param("slug") String slug, @Param("status") PostStatus status);

    /**
     * Find by post year and month and day and slug.
     *
     * @param year  post create year
     * @param month post create month
     * @param day   post create day
     * @param slug  post slug
     * @return a optional of post
     */
    @Select("select post from Post post where year(post.createTime) = :year and month(post"
            + ".createTime) = :month and day(post.createTime) = :day and post.slug = :slug")
    Optional<Post> findBy(@Param("year") Integer year, @Param("month") Integer month,
                          @Param("day") Integer day, @Param("slug") String slug);

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
    @Select("select post from Post post where year(post.createTime) = :year and month(post"
            + ".createTime) = :month and day(post.createTime) = :day and post.slug = :slug and post"
            + ".status = :status")
    Optional<Post> findBy(@Param("year") Integer year, @Param("month") Integer month,
                          @Param("day") Integer day, @Param("slug") String slug, @Param("status") PostStatus status);


    IPage<Post> findAllByIdIn(Collection<Integer> postIds, IPage page);

}
