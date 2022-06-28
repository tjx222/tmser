package com.tmser.blog.repository;

import com.tmser.blog.model.entity.CommentBlackList;
import com.tmser.blog.repository.base.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

/**
 * 评论黑名单Repository
 *
 * @author Lei XinXin
 * @date 2020/1/3
 */
@Mapper
public interface CommentBlackListRepository extends BaseRepository<CommentBlackList> {

    /**
     * 根据IP地址获取数据.
     *
     * @param ipAddress ip address
     * @return comment black list or empty
     */
    Optional<CommentBlackList> findByIpAddress(String ipAddress);

    /**
     * Update Comment BlackList By IPAddress.
     *
     * @param commentBlackList comment black list
     * @return result
     */
    @Select("UPDATE CommentBlackList SET banTime=:#{#commentBlackList.banTime} WHERE "
            + "ipAddress=:#{#commentBlackList.ipAddress}")
    int updateByIpAddress(@Param("commentBlackList") CommentBlackList commentBlackList);
}
