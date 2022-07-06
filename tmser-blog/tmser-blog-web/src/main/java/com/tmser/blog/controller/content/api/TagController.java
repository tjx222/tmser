package com.tmser.blog.controller.content.api;

import com.tmser.blog.model.dto.TagDTO;
import com.tmser.blog.model.entity.Post;
import com.tmser.blog.model.entity.Tag;
import com.tmser.blog.model.enums.PostStatus;
import com.tmser.blog.model.vo.PostListVO;
import com.tmser.blog.service.PostService;
import com.tmser.blog.service.PostTagService;
import com.tmser.blog.service.TagService;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import com.tmser.model.sort.Sort;
import com.tmser.spring.web.PageableDefault;
import com.tmser.spring.web.SortDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Content tag controller.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-04-02
 */
@RestController("ApiContentTagController")
@RequestMapping("/api/content/tags")
public class TagController {

    private final TagService tagService;

    private final PostTagService postTagService;

    private final PostService postService;

    public TagController(TagService tagService,
                         PostTagService postTagService,
                         PostService postService) {
        this.tagService = tagService;
        this.postTagService = postTagService;
        this.postService = postService;
    }

    @GetMapping
    public List<? extends TagDTO> listTags(
            @SortDefault(sort = "update_time, DESC") Sort sort,
            @RequestParam(name = "more", required = false, defaultValue = "false") Boolean more) {
        if (more) {
            return postTagService.listTagWithCountDtos(sort);
        }
        return tagService.convertTo(tagService.listAll(sort));
    }

    @GetMapping("{slug}/posts")
    public Page<PostListVO> listPostsBy(@PathVariable("slug") String slug,
                                        @PageableDefault(sort = {"top_priority,DESC", "update_time,DESC"})
                                                PageImpl pageable) {
        // Get tag by slug
        Tag tag = tagService.getBySlugOfNonNull(slug);

        // Get posts, convert and return
        Page<Post> postPage =
                postTagService.pagePostsBy(tag.getId(), PostStatus.PUBLISHED, pageable);
        return postService.convertToListVo(postPage);
    }
}
