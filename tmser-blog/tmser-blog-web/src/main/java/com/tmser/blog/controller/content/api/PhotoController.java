package com.tmser.blog.controller.content.api;

import com.tmser.blog.model.dto.PhotoDTO;
import com.tmser.blog.model.params.PhotoQuery;
import com.tmser.blog.service.PhotoService;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import com.tmser.model.sort.Sort;
import com.tmser.spring.web.PageableDefault;
import com.tmser.spring.web.SortDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Content photo controller.
 *
 * @author ryanwang
 * @date 2019-09-21
 */
@RestController("ApiContentPhotoController")
@RequestMapping("/api/content/photos")
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    /**
     * List all photos
     *
     * @param sort sort
     * @return all of photos
     */
    @GetMapping(value = "latest")
    public List<PhotoDTO> listPhotos(
            @SortDefault(sort = "update_time,DESC") Sort sort) {
        return photoService.listDtos(sort);
    }

    @GetMapping
    public Page<PhotoDTO> pageBy(
            @PageableDefault(sort = "update_time, DESC") PageImpl pageable,
            PhotoQuery photoQuery) {
        return photoService.pageDtosBy(pageable, photoQuery);
    }

    @GetMapping("teams")
    public List<String> listTeams() {
        return photoService.listAllTeams();
    }
}
