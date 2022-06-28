package com.tmser.blog.service;

import com.tmser.blog.model.dto.PhotoDTO;
import com.tmser.blog.model.entity.Photo;
import com.tmser.blog.model.params.PhotoParam;
import com.tmser.blog.model.params.PhotoQuery;
import com.tmser.blog.model.vo.PhotoTeamVO;
import com.tmser.blog.service.base.CrudService;
import com.tmser.model.page.Page;
import com.tmser.model.sort.Sort;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * Photo service interface.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-03-14
 */
public interface PhotoService extends CrudService<Photo, Integer> {

    /**
     * List photo dtos.
     *
     * @param sort sort
     * @return all photos
     */
    List<PhotoDTO> listDtos(@NonNull Sort sort);

    /**
     * Lists photo team vos.
     *
     * @param sort must not be null
     * @return a list of photo team vo
     */
    List<PhotoTeamVO> listTeamVos(@NonNull Sort sort);

    /**
     * List photos by team.
     *
     * @param team team
     * @param sort sort
     * @return list of photos
     */
    List<PhotoDTO> listByTeam(@NonNull String team, Sort sort);

    /**
     * Pages photo output dtos.
     *
     * @param pageable page info must not be null
     * @return a page of photo output dto
     */
    Page<PhotoDTO> pageBy(@NonNull Page pageable);

    /**
     * Pages photo output dtos.
     *
     * @param pageable   page info must not be null
     * @param photoQuery photoQuery
     * @return a page of photo output dto
     */
    @NonNull
    Page<PhotoDTO> pageDtosBy(@NonNull Page pageable, PhotoQuery photoQuery);

    /**
     * Creates photo by photo param.
     *
     * @param photoParam must not be null
     * @return create photo
     */
    @NonNull
    Photo createBy(@NonNull PhotoParam photoParam);

    /**
     * List all teams.
     *
     * @return list of teams
     */
    List<String> listAllTeams();

    /**
     * Increases photo likes(1).
     *
     * @param photoId photo id must not be null
     */
    void increaseLike(Integer photoId);
}
