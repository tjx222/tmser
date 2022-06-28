package com.tmser.blog.controller.admin.api;

import com.tmser.blog.model.dto.TagDTO;
import com.tmser.blog.model.entity.Tag;
import com.tmser.blog.model.params.TagParam;
import com.tmser.blog.service.PostTagService;
import com.tmser.blog.service.TagService;
import com.tmser.model.sort.Sort;
import com.tmser.spring.web.SortDefault;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Tag controller.
 *
 * @author johnniang
 * @date 3/20/19
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/tags")
public class TagController {

    private final TagService tagService;

    private final PostTagService postTagService;

    public TagController(TagService tagService,
                         PostTagService postTagService) {
        this.tagService = tagService;
        this.postTagService = postTagService;
    }

    @GetMapping
    public List<? extends TagDTO> listTags(
            @SortDefault(sort = "createTime,DESC") Sort sort,
            @RequestParam(name = "more", required = false, defaultValue = "false") Boolean more) {
        if (more) {
            return postTagService.listTagWithCountDtos(sort);
        }
        return tagService.convertTo(tagService.listAll(sort));
    }

    @PostMapping
    public TagDTO createTag(@Valid @RequestBody TagParam tagParam) {
        // Convert to tag
        Tag tag = tagParam.convertTo();

        log.debug("Tag to be created: [{}]", tag);

        // Create and convert
        return tagService.convertTo(tagService.create(tag));
    }

    @GetMapping("{tagId:\\d+}")
    public TagDTO getBy(@PathVariable("tagId") Integer tagId) {
        return tagService.convertTo(tagService.getById(tagId));
    }

    @PutMapping("{tagId:\\d+}")
    public TagDTO updateBy(@PathVariable("tagId") Integer tagId,
                           @Valid @RequestBody TagParam tagParam) {
        // Get old tag
        Tag tag = tagService.getById(tagId);

        // Update tag
        tagParam.update(tag);

        // Update tag
        return tagService.convertTo(tagService.update(tag));
    }

    @DeleteMapping("{tagId:\\d+}")
    public TagDTO deletePermanently(@PathVariable("tagId") Integer tagId) {
        // Remove the tag
        Tag deletedTag = tagService.removeById(tagId);
        // Remove the post tag relationship
        postTagService.removeByTagId(tagId);

        return tagService.convertTo(deletedTag);
    }
}
