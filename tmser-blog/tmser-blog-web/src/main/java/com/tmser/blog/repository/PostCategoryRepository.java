package com.tmser.blog.repository;

import com.tmser.blog.model.entity.PostCategory;
import com.tmser.blog.model.enums.PostStatus;
import com.tmser.blog.repository.base.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Set;


/**
 * Post category repository.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-03-19
 */
@Mapper
public interface PostCategoryRepository extends BaseRepository<PostCategory> {

    /**
     * Finds all category ids by post id
     *
     * @param postId post id must not be null
     * @return a list of category id
     */
    @NonNull
    @Select("select category_id from post_categories  where post_id = #{postId}")
    Set<Integer> findAllCategoryIdsByPostId(@NonNull @Param("postId") Integer postId);

    /**
     * Finds all post ids by category id.
     *
     * @param categoryId category id must not be null
     * @return a set of post id
     */
    @NonNull
    @Select("select post_id from post_categories where category_id = #{categoryId}")
    Set<Integer> findAllPostIdsByCategoryId(@NonNull @Param("categoryId") Integer categoryId);

    /**
     * Finds all post ids by category id and post status.
     *
     * @param categoryId category id must not be null
     * @param status     post status must not be null
     * @return a set of post id
     */
    @NonNull
    @Select("select postCategory.post_id from post_categories postCategory, posts post where "
            + "postCategory.category_id = #{categoryId} and post.id = postCategory.post_id and post.status = #{status}")
    Set<Integer> findAllPostIdsByCategoryIdAndStatus(@NonNull @Param("categoryId")  Integer categoryId,
                                            @NonNull @Param("status") PostStatus status);

    /**
     * Finds all post ids by category id and post status.
     *
     * @param categoryId category id must not be null
     * @param statuses     post status must not be empty
     * @return a set of post id
     */
    @NonNull
    @Select({"<script>","select p.post_id from post_categories p, posts post where" +
            " p.category_id = #{categoryId} and post.id = p.post_id and post.status in",
            "<foreach item='item' index='index' collection='statuses' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"})
    Set<Integer> findAllPostIdsByCategoryIdAndStatuses(
            @NonNull@Param("categoryId")  Integer categoryId, @Param("statuses") @NonNull Set<PostStatus> statuses);

    /**
     * Finds all post categories by post id in.
     *
     * @param postIds post id collection must not be null
     * @return a list of post category
     */
    @NonNull
    @Select({"<script>","select * from post_categories  where post_id in",
            "<foreach item='item' index='index' collection='postIds' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"})
    List<PostCategory> findAllByPostIdIn(@NonNull @Param("postIds") Collection<Integer> postIds);

    /**
     * Finds all post categories by post id.
     *
     * @param postId post id must not be null
     * @return a list of post category
     */
    @NonNull
    @Select("select * from post_categories where post_id = #{postId}")
    List<PostCategory> findAllByPostId(@NonNull @Param("postId") Integer postId);

    /**
     * Finds all post categories by category id.
     *
     * @param categoryId category id must not be null
     * @return a list of post category
     */
    @NonNull
    @Select("select * from post_categories where category_id = #{categoryId}")
    List<PostCategory> findAllByCategoryId(@NonNull @Param("categoryId") Integer categoryId);

    /**
     * Deletes post categories by post id.
     *
     * @param postId post id must not be null
     * @return a list of post category deleted
     */
    @NonNull
    @Update("delete from post_categories where post_id = #{postId}")
    void deleteByPostId(@NonNull Integer postId);

    /**
     * Deletes post categories by category id.
     *
     * @param categoryId category id must not be null
     * @return a list of post category deleted
     */
    @NonNull
    @Update("delete from post_categories where category_id = #{categoryId}")
    void deleteByCategoryId(@NonNull @Param("categoryId") Integer categoryId);

    /**
     * Finds all post categories by category id list.
     *
     * @param categoryIdList category id list must not be empty
     * @return a list of post category
     */
    @Select({"<script>","select * from post_categories  where category_id in",
            "<foreach item='item' index='index' collection='categoryIdList' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"})
    @NonNull
    List<PostCategory> findAllByCategoryIdList(@Param("categoryIdList") List<Integer> categoryIdList);
}
