package com.tmser.blog.repository;

import com.tmser.blog.model.entity.Menu;
import com.tmser.blog.repository.base.BaseRepository;
import com.tmser.model.sort.Sort;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * Menu repository.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-8-28
 */
@Mapper
public interface MenuRepository extends BaseRepository<Menu> {

    /**
     * Query if the menu name already exists
     *
     * @param name name must not be null.
     * @return true or false
     */
    @Select(value = "select 1  from menus where name = #{name} limit 1")
    boolean existsByName(@NonNull String name);

    /**
     * Query if the menu name already exists by id and name.
     *
     * @param id   id must not be null.
     * @param name name must not be null.
     * @return true or false.
     */
    @Select(value = "select 1  from menus where name = #{name} and id <> #{id} limit 1")
    boolean existsByIdNotAndName(@Param("id")@NonNull Integer id, @Param("name")@NonNull String name);

    /**
     * Finds by menu parent id.
     *
     * @param id parent id must not be null.
     * @return a list of menu.
     */
    @Select(value = "select * from menus where parent_id = #{id}")
    List<Menu> findByParentId(@NonNull @Param("id") Integer id);

    /**
     * Finds by menu team.
     *
     * @param team team must not be null.
     * @param sort sort.
     * @return a list of menu
     */
    @Select(value = "select * from menus where team = #{team} order by #{sort}")
    List<Menu> findByTeam(@NonNull @Param("team") String team, @Param("sort") Sort sort);

    /**
     * Find all menu teams.
     *
     * @return a list of teams
     */
    @Select(value = "select distinct a.team from menus a")
    List<String> findAllTeams();
}
