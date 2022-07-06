package com.tmser.blog.repository;

import com.tmser.blog.model.entity.Link;
import com.tmser.blog.repository.base.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Link repository.
 *
 * @author johnniang
 */
@Mapper
public interface LinkRepository extends BaseRepository<Link> {

    /**
     * Find all link teams.
     *
     * @return a list of teams
     */
    @Select(value = "select distinct a.team from links a")
    List<String> findAllTeams();

    @Select(value = "select count(*)  from links where name = #{name} and id <> #{id} limit 1")
    boolean existsByNameAndIdNot(@Param("name") String name, @Param("id") Integer id);

    @Select(value = "select count(*)  from links where url = #{url} and id <> #{id} limit 1")
    boolean existsByUrlAndIdNot(@Param("url") String url, @Param("id") Integer id);
}
