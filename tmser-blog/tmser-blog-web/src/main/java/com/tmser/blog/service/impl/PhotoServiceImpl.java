package com.tmser.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tmser.blog.exception.BadRequestException;
import com.tmser.blog.model.dto.PhotoDTO;
import com.tmser.blog.model.entity.Photo;
import com.tmser.blog.model.params.PhotoParam;
import com.tmser.blog.model.params.PhotoQuery;
import com.tmser.blog.model.vo.PhotoTeamVO;
import com.tmser.blog.repository.PhotoRepository;
import com.tmser.blog.service.PhotoService;
import com.tmser.blog.service.base.AbstractCrudService;
import com.tmser.blog.utils.ServiceUtils;
import com.tmser.database.mybatis.MybatisPageHelper;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import com.tmser.model.sort.Sort;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * PhotoService implementation class.
 *
 * @author ryanwang
 * @author guqing
 * @date 2019-03-14
 */
@Slf4j
@Service
public class PhotoServiceImpl extends AbstractCrudService<Photo, Integer> implements PhotoService {

    private final PhotoRepository photoRepository;

    public PhotoServiceImpl(PhotoRepository photoRepository) {
        super(photoRepository);
        this.photoRepository = photoRepository;
    }

    @Override
    public List<PhotoDTO> listDtos(Sort sort) {
        Assert.notNull(sort, "Sort info must not be null");

        return listAll(sort).stream().map(photo -> (PhotoDTO) new PhotoDTO().convertFrom(photo))
                .collect(Collectors.toList());
    }

    @Override
    public List<PhotoTeamVO> listTeamVos(Sort sort) {
        Assert.notNull(sort, "Sort info must not be null");

        // List all photos
        List<PhotoDTO> photos = listDtos(sort);

        // Get teams
        Set<String> teams = ServiceUtils.fetchProperty(photos, PhotoDTO::getTeam);

        Map<String, List<PhotoDTO>> teamPhotoListMap =
                ServiceUtils.convertToListMap(teams, photos, PhotoDTO::getTeam);

        List<PhotoTeamVO> result = new LinkedList<>();

        // Wrap photo team vo list
        teamPhotoListMap.forEach((team, photoList) -> {
            // Build photo team vo
            PhotoTeamVO photoTeamVO = new PhotoTeamVO();
            photoTeamVO.setTeam(team);
            photoTeamVO.setPhotos(photoList);

            // Add it to result
            result.add(photoTeamVO);
        });

        return result;
    }

    @Override
    public List<PhotoDTO> listByTeam(String team, Sort sort) {
        List<Photo> photos = photoRepository.findByTeam(team, sort);
        return photos.stream().map(photo -> (PhotoDTO) new PhotoDTO().convertFrom(photo))
                .collect(Collectors.toList());
    }

    @Override
    public Page<PhotoDTO> pageBy(Page pageable) {
        Assert.notNull(pageable, "Page info must not be null");

        Page<Photo> photos = MybatisPageHelper.fillPageData(
                photoRepository.selectPage(MybatisPageHelper.changeToMybatisPage(pageable), null), pageable);
        Page<PhotoDTO> pageResult = PageImpl.of(photos.getCurrent(), photos.getSize(), photos.getTotal());
        return pageResult.setContent(photos.getContent().stream()
                .map(photo -> new PhotoDTO().<PhotoDTO>convertFrom(photo)).collect(Collectors.toList()));
    }

    @Override
    public Page<PhotoDTO> pageDtosBy(Page pageable, PhotoQuery photoQuery) {
        Assert.notNull(pageable, "Page info must not be null");

        QueryWrapper<Photo> wrapper = new QueryWrapper<>();
        if (photoQuery.getTeam() != null) {
            wrapper.eq("team", photoQuery.getTeam());
        }

        if (photoQuery.getKeyword() != null) {

            String likeCondition = StringUtils.strip(photoQuery.getKeyword());
            wrapper.and(wp -> wp.like("name", likeCondition)
                    .or().like("description", likeCondition)
                    .or().like("location", likeCondition)
            );
        }
        // List all
        Page<Photo> photoPage = MybatisPageHelper.fillPageData(
                photoRepository.selectPage(MybatisPageHelper.changeToMybatisPage(pageable), wrapper), pageable);
        Page<PhotoDTO> resultPage = PageImpl.of(photoPage.getCurrent(), photoPage.getSize(), photoPage.getTotal());
        // Convert and return
        return resultPage.setContent(photoPage.getContent().stream()
                .map(photo -> new PhotoDTO().<PhotoDTO>convertFrom(photo)).collect(Collectors.toList()));
    }


    @Override
    public Photo createBy(PhotoParam photoParam) {
        Assert.notNull(photoParam, "Photo param must not be null");

        return create(photoParam.convertTo());
    }

    @Override
    public List<String> listAllTeams() {
        return photoRepository.findAllTeams();
    }

    @Override
    @Transactional
    public void increaseLike(Integer photoId) {
        Assert.notNull(photoId, "Photo id must not be null");

        int affectedRows = photoRepository.updateLikes(1L, photoId);

        if (affectedRows != 1) {
            log.error("Photo with id: [{}] may not be found", photoId);
            throw new BadRequestException(
                    "Failed to increase likes 1 for photo with id " + photoId);
        }
    }


}
