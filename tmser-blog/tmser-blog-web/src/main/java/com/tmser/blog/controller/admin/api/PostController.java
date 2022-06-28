package com.tmser.blog.controller.admin.api;

import com.tmser.blog.cache.AbstractStringCacheStore;
import com.tmser.blog.model.dto.post.BasePostDetailDTO;
import com.tmser.blog.model.dto.post.BasePostMinimalDTO;
import com.tmser.blog.model.dto.post.BasePostSimpleDTO;
import com.tmser.blog.model.entity.Post;
import com.tmser.blog.model.enums.PostStatus;
import com.tmser.blog.model.params.PostContentParam;
import com.tmser.blog.model.params.PostParam;
import com.tmser.blog.model.params.PostQuery;
import com.tmser.blog.model.vo.PostDetailVO;
import com.tmser.blog.service.OptionService;
import com.tmser.blog.service.PostService;
import com.tmser.blog.utils.HaloUtils;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import com.tmser.spring.web.PageableDefault;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Post controller.
 *
 * @author johnniang
 * @author ryanwang
 * @author guqing
 * @date 2019-03-19
 */
@RestController
@RequestMapping("/api/admin/posts")
public class PostController {

    private final PostService postService;

    private final AbstractStringCacheStore cacheStore;

    private final OptionService optionService;

    public PostController(PostService postService,
                          AbstractStringCacheStore cacheStore,
                          OptionService optionService) {
        this.postService = postService;
        this.cacheStore = cacheStore;
        this.optionService = optionService;
    }

    @GetMapping
    public Page<? extends BasePostSimpleDTO> pageBy(
            @PageableDefault(sort = {"topPriority,DESC", "createTime,DESC"}) PageImpl pageable,
            PostQuery postQuery,
            @RequestParam(value = "more", defaultValue = "true") Boolean more) {
        Page<Post> postPage = postService.pageBy(postQuery, pageable);
        if (more) {
            return postService.convertToListVo(postPage, true);
        }

        return postService.convertToSimple(postPage);
    }

    @GetMapping("latest")
    public List<BasePostMinimalDTO> pageLatest(
            @RequestParam(name = "top", defaultValue = "10") int top) {
        return postService.convertToMinimal(postService.pageLatest(top).getContent());
    }

    @GetMapping("status/{status}")
    public Page<? extends BasePostSimpleDTO> pageByStatus(
            @PathVariable(name = "status") PostStatus status,
            @RequestParam(value = "more", required = false, defaultValue = "false") Boolean more,
            @PageableDefault(sort = "createTime, DESC") PageImpl pageable) {
        Page<Post> posts = postService.pageBy(status, pageable);

        if (more) {
            return postService.convertToListVo(posts, true);
        }

        return postService.convertToSimple(posts);
    }

    @GetMapping("{postId:\\d+}")
    public PostDetailVO getBy(@PathVariable("postId") Integer postId) {
        Post post = postService.getWithLatestContentById(postId);
        return postService.convertToDetailVo(post, true);
    }

    @PutMapping("{postId:\\d+}/likes")
    public void likes(@PathVariable("postId") Integer postId) {
        postService.increaseLike(postId);
    }

    @PostMapping
    public PostDetailVO createBy(@Valid @RequestBody PostParam postParam,
                                 @RequestParam(value = "autoSave", required = false, defaultValue = "false") Boolean autoSave
    ) {
        // Convert to
        Post post = postParam.convertTo();
        return postService.createBy(post, postParam.getTagIds(), postParam.getCategoryIds(),
                postParam.getPostMetas(), autoSave);
    }

    @PutMapping("{postId:\\d+}")
    public PostDetailVO updateBy(@Valid @RequestBody PostParam postParam,
                                 @PathVariable("postId") Integer postId,
                                 @RequestParam(value = "autoSave", required = false, defaultValue = "false") Boolean autoSave
    ) {
        // Get the post info
        Post postToUpdate = postService.getWithLatestContentById(postId);

        postParam.update(postToUpdate);
        return postService.updateBy(postToUpdate, postParam.getTagIds(), postParam.getCategoryIds(),
                postParam.getPostMetas(), autoSave);
    }

    @PutMapping("{postId:\\d+}/status/{status}")
    public BasePostMinimalDTO updateStatusBy(
            @PathVariable("postId") Integer postId,
            @PathVariable("status") PostStatus status) {
        Post post = postService.updateStatus(status, postId);

        return new BasePostMinimalDTO().convertFrom(post);
    }

    @PutMapping("status/{status}")
    public List<Post> updateStatusInBatch(@PathVariable(name = "status") PostStatus status,
                                          @RequestBody List<Integer> ids) {
        return postService.updateStatusByIds(ids, status);
    }

    @PutMapping("{postId:\\d+}/status/draft/content")
    public BasePostDetailDTO updateDraftBy(
            @PathVariable("postId") Integer postId,
            @RequestBody PostContentParam contentParam) {
        Post postToUse = postService.getById(postId);
        String formattedContent = contentParam.decideContentBy(postToUse.getEditorType());
        // Update draft content
        Post post = postService.updateDraftContent(formattedContent,
                contentParam.getOriginalContent(), postId);
        return postService.convertToDetail(post);
    }

    @DeleteMapping("{postId:\\d+}")
    public void deletePermanently(@PathVariable("postId") Integer postId) {
        postService.removeById(postId);
    }

    @DeleteMapping
    public List<Post> deletePermanentlyInBatch(@RequestBody List<Integer> ids) {
        return postService.removeByIds(ids);
    }

    @GetMapping(value = {"preview/{postId:\\d+}", "{postId:\\d+}/preview"})
    public String preview(@PathVariable("postId") Integer postId)
            throws UnsupportedEncodingException, URISyntaxException {
        Post post = postService.getById(postId);

        post.setSlug(URLEncoder.encode(post.getSlug(), StandardCharsets.UTF_8.name()));

        BasePostMinimalDTO postMinimalDTO = postService.convertToMinimal(post);

        String token = HaloUtils.simpleUUID();

        // cache preview token
        cacheStore.putAny(token, token, 10, TimeUnit.MINUTES);

        StringBuilder previewUrl = new StringBuilder();

        if (!optionService.isEnabledAbsolutePath()) {
            previewUrl.append(optionService.getBlogBaseUrl());
        }

        previewUrl.append(postMinimalDTO.getFullPath());

        // build preview post url and return
        return new URIBuilder(previewUrl.toString())
                .addParameter("token", token)
                .build().toString();
    }
}
