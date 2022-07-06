package com.tmser.blog.repository;

import com.tmser.blog.model.entity.PostTag;
import com.tmser.blog.model.enums.PostStatus;
import com.tmser.blog.model.projection.TagPostPostCountProjection;
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
 * Post tag repository.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-03-19
 */
@Mapper
public interface PostTagRepository extends BaseRepository<PostTag> {

    /**
     * Finds all post tags by post id.
     *
     * @param postId post id must not be null
     * @return a list of post tags
     */
    @NonNull
    @Select("select * from post_tags where post_id = #{postId}")
    List<PostTag> findAllByPostId(@NonNull @Param("postId") Integer postId);

    /**
     * Finds all tag ids by post id.
     *
     * @param postId post id must not be null
     * @return a set of tag id
     */
    @Select("select postTag.tag_id from post_tags postTag where postTag.post_id = #{postId}")
    @NonNull
    Set<Integer> findAllTagIdsByPostId(@NonNull @Param("postId") Integer postId);

    /**
     * Finds all post tags by tag id.
     *
     * @param tagId tag id must not be null
     * @return a list of post tags
     */
    @NonNull
    @Select("select * from post_tags where tag_id = #{tagId}")
    List<PostTag> findAllByTagId(@NonNull @Param("tagId") Integer tagId);

    /**
     * Finds all post id by tag id.
     *
     * @param tagId tag id must not be null
     * @return a set of post id
     */
    @Select("select postTag.post_id from post_tags postTag where postTag.tag_id = #{tagId}")
    @NonNull
    Set<Integer> findAllPostIdsByTagId(@NonNull @Param("tagId") Integer tagId);

    /**
     * Finds all post id by tag id and post status.
     *
     * @param tagId  tag id must not be null
     * @param status post status
     * @return a set of post id
     */
    @Select("select postTag.post_id from post_tags postTag, posts post where postTag.tag_id = #{tagId} and "
            + "post.id = postTag.post_id and post.status = #{status}")
    @NonNull
    Set<Integer> findAllPostIdsByTagIdAndStatus(@NonNull @Param("tagId") Integer tagId, @NonNull @Param("status") PostStatus status);

    /**
     * Finds all tags by post id in.
     *
     * @param postIds post id collection
     * @return a list of post tags
     */
    @NonNull
    @Select({"<script>"," select * from post_tags where post_id in ",
            "<foreach item='item' index='index' collection='postIds' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach></script>"})
    List<PostTag> findAllByPostIdIn(@NonNull @Param("postIds") Collection<Integer> postIds);

    /**
     * Deletes post tags by post id.
     *
     * @param postId post id must not be null
     * @return a list of post tag deleted
     */
    @NonNull
    @Update("delete from post_tags where post_id = #{postId}")
    void deleteByPostId(@NonNull@Param("postId") Integer postId);

    /**
     * Deletes post tags by tag id.
     *
     * @param tagId tag id must not be null
     * @return a list of post tag deleted
     */
    @NonNull
    @Update("delete from post_tags where tag_id = #{tagId}")
    void deleteByTagId(@NonNull @Param("tagId") Integer tagId);

    /**
     * Finds post count by tag id collection.
     *
     * @param tagIds tag id collection must not be null
     * @return a list of tag post count projection
     */
    @NonNull
    @Select({"<script>","select count(post_id), tag_id from post_tags where post_id in ",
            "<foreach item='item' index='index' collection='tagIds' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "group by tag_id",
            "</script>"})
    List<TagPostPostCountProjection> findPostCountByTagIds(@NonNull @Param("tagIds") Collection<Integer> tagIds);

    /**
     * Finds post count of tag.
     *
     * @return a list of tag post count projection
     */
    @NonNull
    @Select("select count(post_id), tag_id from post_tags group by tag_id")
    List<TagPostPostCountProjection> findPostCount();
}
