package com.tmser.blog.controller.content.api;

import com.google.common.collect.Sets;
import com.tmser.blog.exception.ForbiddenException;
import com.tmser.blog.model.dto.CategoryDTO;
import com.tmser.blog.model.entity.Category;
import com.tmser.blog.model.entity.Post;
import com.tmser.blog.model.enums.PostStatus;
import com.tmser.blog.model.vo.PostListVO;
import com.tmser.blog.service.AuthenticationService;
import com.tmser.blog.service.CategoryService;
import com.tmser.blog.service.PostCategoryService;
import com.tmser.blog.service.PostService;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import com.tmser.model.sort.Sort;
import com.tmser.spring.web.PageableDefault;
import com.tmser.spring.web.SortDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Content category controller.
 *
 * @author ryanwang
 * @date 2019-06-09
 */
@RestController("ApiContentCategoryController")
@RequestMapping("/api/content/categories")
public class CategoryController {

    private final CategoryService categoryService;

    private final PostCategoryService postCategoryService;

    private final PostService postService;

    private final AuthenticationService authenticationService;

    public CategoryController(CategoryService categoryService,
                              PostCategoryService postCategoryService,
                              PostService postService,
                              AuthenticationService authenticationService) {
        this.categoryService = categoryService;
        this.postCategoryService = postCategoryService;
        this.postService = postService;
        this.authenticationService = authenticationService;
    }

    @GetMapping
    public List<? extends CategoryDTO> listCategories(
            @SortDefault(sort = "updateTime, DESC") Sort sort,
            @RequestParam(name = "more", required = false, defaultValue = "false") Boolean more) {
        if (more) {
            return postCategoryService.listCategoryWithPostCountDto(sort, false);
        }
        return categoryService.convertTo(categoryService.listAll(sort));
    }

    @GetMapping("{slug}/posts")
    public Page<PostListVO> listPostsBy(@PathVariable("slug") String slug,
                                        @RequestParam(value = "password", required = false) String password,
                                        @PageableDefault(sort = {"topPriority,DESC", "updateTime,DESC"})
                                                PageImpl pageable) {
        // Get category by slug
        Category category = categoryService.getBySlugOfNonNull(slug, true);

        if (!authenticationService.categoryAuthentication(category.getId(), password)) {
            throw new ForbiddenException("您没有该分类的访问权限");
        }

        Page<Post> postPage =
                postCategoryService.pagePostBy(category.getId(),
                        Sets.immutableEnumSet(PostStatus.PUBLISHED), pageable);
        return postService.convertToListVo(postPage);
    }
}
