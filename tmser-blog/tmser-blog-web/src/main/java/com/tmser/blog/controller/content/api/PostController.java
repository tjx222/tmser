package com.tmser.blog.controller.content.api;

import com.google.common.collect.Sets;
import com.tmser.blog.cache.lock.CacheLock;
import com.tmser.blog.cache.lock.CacheParam;
import com.tmser.blog.exception.NotFoundException;
import com.tmser.blog.model.dto.BaseCommentDTO;
import com.tmser.blog.model.dto.post.BasePostSimpleDTO;
import com.tmser.blog.model.entity.Post;
import com.tmser.blog.model.entity.PostComment;
import com.tmser.blog.model.enums.CommentStatus;
import com.tmser.blog.model.enums.PostStatus;
import com.tmser.blog.model.params.PostCommentParam;
import com.tmser.blog.model.params.PostQuery;
import com.tmser.blog.model.vo.*;
import com.tmser.blog.service.OptionService;
import com.tmser.blog.service.PostCommentService;
import com.tmser.blog.service.PostService;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import com.tmser.model.sort.Sort;
import com.tmser.spring.web.PageableDefault;
import com.tmser.spring.web.SortDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Content post controller.
 *
 * @author johnniang
 * @author ryanwang
 * @author guqing
 * @date 2019-04-02
 */
@RestController("ApiContentPostController")
@RequestMapping("/api/content/posts")
public class PostController {

    private final PostService postService;

    private final PostCommentService postCommentService;

    private final OptionService optionService;

    public PostController(PostService postService,
                          PostCommentService postCommentService,
                          OptionService optionService) {
        this.postService = postService;
        this.postCommentService = postCommentService;
        this.optionService = optionService;
    }

    //CS304 issue for https://github.com/halo-dev/halo/issues/1351

    /**
     * Enable users search published articles with keywords
     *
     * @param pageable   store the priority of the sort algorithm
     * @param keyword    search articles with keyword
     * @param categoryId search articles with categoryId
     * @return published articles that contains keywords and specific categoryId
     */
    @GetMapping
    public Page<PostListVO> pageBy(
            @PageableDefault(sort = {"topPriority,DESC", "createTime,DESC"}) PageImpl pageable,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Integer categoryId) {
        PostQuery postQuery = new PostQuery();
        postQuery.setKeyword(keyword);
        postQuery.setCategoryId(categoryId);
        postQuery.setStatuses(Sets.newHashSet(PostStatus.PUBLISHED));
        Page<Post> postPage = postService.pageBy(postQuery, pageable);
        return postService.convertToListVo(postPage, true);
    }

    @PostMapping(value = "search")
    public Page<BasePostSimpleDTO> pageBy(@RequestParam(value = "keyword") String keyword,
                                          @PageableDefault(sort = "createTime, DESC") PageImpl pageable) {
        Page<Post> postPage = postService.pageBy(keyword, pageable);
        return postService.convertToSimple(postPage);
    }

    @GetMapping("{postId:\\d+}")
    public PostDetailVO getBy(@PathVariable("postId") Integer postId,
                              @RequestParam(value = "formatDisabled", required = false, defaultValue = "true")
                                      Boolean formatDisabled,
                              @RequestParam(value = "sourceDisabled", required = false, defaultValue = "false")
                                      Boolean sourceDisabled) {
        PostDetailVO postDetailVO = postService.convertToDetailVo(postService.getById(postId));

        if (formatDisabled) {
            // Clear the format content
            postDetailVO.setContent(null);
        }

        if (sourceDisabled) {
            // Clear the original content
            postDetailVO.setOriginalContent(null);
        }

        postService.publishVisitEvent(postDetailVO.getId());

        return postDetailVO;
    }

    @GetMapping("/slug")
    public PostDetailVO getBy(@RequestParam("slug") String slug,
                              @RequestParam(value = "formatDisabled", required = false, defaultValue = "true")
                                      Boolean formatDisabled,
                              @RequestParam(value = "sourceDisabled", required = false, defaultValue = "false")
                                      Boolean sourceDisabled) {
        PostDetailVO postDetailVO = postService.convertToDetailVo(postService.getBySlug(slug));

        if (formatDisabled) {
            // Clear the format content
            postDetailVO.setContent(null);
        }

        if (sourceDisabled) {
            // Clear the original content
            postDetailVO.setOriginalContent(null);
        }

        postService.publishVisitEvent(postDetailVO.getId());

        return postDetailVO;
    }

    @GetMapping("{postId:\\d+}/prev")
    public PostDetailVO getPrevPostBy(@PathVariable("postId") Integer postId) {
        Post post = postService.getById(postId);
        Post prevPost =
                postService.getPrevPost(post).orElseThrow(() -> new NotFoundException("查询不到该文章的信息"));
        return postService.convertToDetailVo(prevPost);
    }

    @GetMapping("{postId:\\d+}/next")
    public PostDetailVO getNextPostBy(@PathVariable("postId") Integer postId) {
        Post post = postService.getById(postId);
        Post nextPost =
                postService.getNextPost(post).orElseThrow(() -> new NotFoundException("查询不到该文章的信息"));
        return postService.convertToDetailVo(nextPost);
    }

    @GetMapping("{postId:\\d+}/comments/top_view")
    public Page<CommentWithHasChildrenVO> listTopComments(@PathVariable("postId") Integer postId,
                                                          @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                          @SortDefault(sort = "createTime, DESC") Sort sort) {
        return postCommentService.pageTopCommentsBy(postId, CommentStatus.PUBLISHED,
                PageImpl.of(page, optionService.getCommentPageSize(), sort));
    }

    @GetMapping("{postId:\\d+}/comments/{commentParentId:\\d+}/children")
    public List<BaseCommentDTO> listChildrenBy(@PathVariable("postId") Integer postId,
                                               @PathVariable("commentParentId") Long commentParentId,
                                               @SortDefault(sort = "createTime,DESC") Sort sort) {
        // Find all children comments
        List<PostComment> postComments = postCommentService
                .listChildrenBy(postId, commentParentId, CommentStatus.PUBLISHED, sort);
        // Convert to base comment dto

        return postCommentService.convertTo(postComments);
    }

    @GetMapping("{postId:\\d+}/comments/tree_view")
    public Page<BaseCommentVO> listCommentsTree(@PathVariable("postId") Integer postId,
                                                @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                @SortDefault(sort = "createTime, DESC") Sort sort) {
        return postCommentService
                .pageVosBy(postId, PageImpl.of(page, optionService.getCommentPageSize(), sort));
    }

    @GetMapping("{postId:\\d+}/comments/list_view")
    public Page<BaseCommentWithParentVO> listComments(@PathVariable("postId") Integer postId,
                                                      @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                      @SortDefault(sort = "createTime,DESC") Sort sort) {
        return postCommentService.pageWithParentVoBy(postId,
                PageImpl.of(page, optionService.getCommentPageSize(), sort));
    }

    @PostMapping("comments")
    @CacheLock(autoDelete = false, traceRequest = true)
    public BaseCommentDTO comment(@RequestBody PostCommentParam postCommentParam) {
        postCommentService.validateCommentBlackListStatus();

        // Escape content
        postCommentParam.setContent(HtmlUtils
                .htmlEscape(postCommentParam.getContent(), StandardCharsets.UTF_8.displayName()));
        return postCommentService.convertTo(postCommentService.createBy(postCommentParam));
    }

    @PostMapping("{postId:\\d+}/likes")
    @CacheLock(autoDelete = false, traceRequest = true)
    public void like(@PathVariable("postId") @CacheParam Integer postId) {
        postService.increaseLike(postId);
    }
}
