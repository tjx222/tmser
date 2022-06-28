package com.tmser.blog.repository;

import com.tmser.blog.model.entity.Photo;
import com.tmser.blog.repository.base.BaseRepository;
import com.tmser.model.sort.Sort;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Photo repository.
 *
 * @author johnniang
 * @author ryanwang
 * @author guqing
 * @date 2019-04-03
 */
@Mapper
public interface PhotoRepository
        extends BaseRepository<Photo> {

    /**
     * Query photos by team
     *
     * @param team team
     * @param sort sort
     * @return list of photo
     */
    List<Photo> findByTeam(String team, Sort sort);

    /**
     * Find all photo teams.
     *
     * @return list of teams.
     */
    @Select(value = "select distinct p.team from Photo p")
    List<String> findAllTeams();

    /**
     * Updates photo likes.
     *
     * @param likes   likes delta
     * @param photoId photo id must not be null
     * @return updated rows
     */
    @Select("update Photo p set p.likes = p.likes + :likes where p.id = :photoId")
    int updateLikes(long likes, Integer photoId);
}
