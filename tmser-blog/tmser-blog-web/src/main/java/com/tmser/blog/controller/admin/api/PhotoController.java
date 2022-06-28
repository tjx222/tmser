package com.tmser.blog.controller.admin.api;

import com.tmser.blog.cache.lock.CacheLock;
import com.tmser.blog.cache.lock.CacheParam;
import com.tmser.blog.model.dto.PhotoDTO;
import com.tmser.blog.model.entity.Photo;
import com.tmser.blog.model.params.PhotoParam;
import com.tmser.blog.model.params.PhotoQuery;
import com.tmser.blog.service.PhotoService;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import com.tmser.model.sort.Sort;
import com.tmser.spring.web.PageableDefault;
import com.tmser.spring.web.SortDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Photo controller
 *
 * @author ryanwang
 * @date 2019-03-21
 */
@Validated
@RestController
@RequestMapping("/api/admin/photos")
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @GetMapping(value = "latest")
    public List<PhotoDTO> listPhotos(
            @SortDefault(sort = "createTime,DESC") Sort sort) {
        return photoService.listDtos(sort);
    }

    @GetMapping
    public Page<PhotoDTO> pageBy(
            @PageableDefault(sort = "createTime,DESC") PageImpl pageable,
            PhotoQuery photoQuery) {
        return photoService.pageDtosBy(pageable, photoQuery);
    }

    @GetMapping("{photoId:\\d+}")
    public PhotoDTO getBy(@PathVariable("photoId") Integer photoId) {
        return new PhotoDTO().convertFrom(photoService.getById(photoId));
    }

    @DeleteMapping("{photoId:\\d+}")
    public void deletePermanently(@PathVariable("photoId") Integer photoId) {
        photoService.removeById(photoId);
    }

    @PostMapping
    public PhotoDTO createBy(@Valid @RequestBody PhotoParam photoParam) {
        return new PhotoDTO().convertFrom(photoService.createBy(photoParam));
    }

    @PostMapping("/batch")
    public List<PhotoDTO> createBatchBy(@RequestBody List<@Valid PhotoParam> photoParams) {
        return photoParams.stream()
                .map(photoParam -> {
                    PhotoDTO photoDto = new PhotoDTO();
                    photoDto.convertFrom(photoService.createBy(photoParam));
                    return photoDto;
                })
                .collect(Collectors.toList());
    }

    @PutMapping("{photoId:\\d+}")
    public PhotoDTO updateBy(@PathVariable("photoId") Integer photoId,
                             @RequestBody @Valid PhotoParam photoParam) {
        // Get the photo
        Photo photo = photoService.getById(photoId);

        // Update changed properties of the photo
        photoParam.update(photo);

        // Update menu in database
        return new PhotoDTO().convertFrom(photoService.update(photo));
    }

    @PutMapping("{photoId:\\d+}/likes")
    @CacheLock(autoDelete = false, traceRequest = true)
    public void likes(@PathVariable @CacheParam Integer photoId) {
        photoService.increaseLike(photoId);
    }

    @GetMapping("teams")
    public List<String> listTeams() {
        return photoService.listAllTeams();
    }
}
