package com.tmser.blog.repository;

import com.tmser.blog.model.entity.Link;
import com.tmser.blog.repository.base.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
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
    @Select(value = "select distinct a.team from Link a")
    List<String> findAllTeams();

    boolean existsByNameAndIdNot(String name, Integer id);

    boolean existsByUrlAndIdNot(String url, Integer id);
}
