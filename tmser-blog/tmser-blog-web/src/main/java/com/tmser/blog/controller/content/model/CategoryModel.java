package com.tmser.blog.controller.content.model;

import com.google.common.collect.Sets;
import com.tmser.blog.model.dto.CategoryDTO;
import com.tmser.blog.model.entity.Category;
import com.tmser.blog.model.entity.Post;
import com.tmser.blog.model.enums.EncryptTypeEnum;
import com.tmser.blog.model.enums.PostStatus;
import com.tmser.blog.model.vo.PostListVO;
import com.tmser.blog.service.*;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import com.tmser.model.sort.Sort;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.Set;

import static com.tmser.blog.model.support.HaloConst.POST_PASSWORD_TEMPLATE;
import static com.tmser.blog.model.support.HaloConst.SUFFIX_FTL;
import static com.tmser.model.sort.Sort.Direction.DESC;

/**
 * Category Model.
 *
 * @author ryanwang
 * @date 2020-01-11
 */
@Component
public class CategoryModel {

    private final CategoryService categoryService;

    private final ThemeService themeService;

    private final PostCategoryService postCategoryService;

    private final PostService postService;

    private final OptionService optionService;

    private final AuthenticationService authenticationService;

    public CategoryModel(CategoryService categoryService,
                         ThemeService themeService,
                         PostCategoryService postCategoryService,
                         PostService postService,
                         OptionService optionService,
                         AuthenticationService authenticationService) {
        this.categoryService = categoryService;
        this.themeService = themeService;
        this.postCategoryService = postCategoryService;
        this.postService = postService;
        this.optionService = optionService;
        this.authenticationService = authenticationService;
    }

    /**
     * List categories.
     *
     * @param model model
     * @return template name
     */
    public String list(Model model) {
        model.addAttribute("is_categories", true);
        model.addAttribute("meta_keywords", optionService.getSeoKeywords());
        model.addAttribute("meta_description", optionService.getSeoDescription());
        return themeService.render("categories");
    }

    /**
     * List category posts.
     *
     * @param model model
     * @param slug  slug
     * @param page  current page
     * @return template name
     */
    public String listPost(Model model, String slug, Integer page) {

        // Get category by slug
        final Category category = categoryService.getBySlugOfNonNull(slug, true);

        if (!authenticationService.categoryAuthentication(category.getId(), null)) {
            model.addAttribute("slug", category.getSlug());
            model.addAttribute("type", EncryptTypeEnum.CATEGORY.getName());
            if (themeService.templateExists(POST_PASSWORD_TEMPLATE + SUFFIX_FTL)) {
                return themeService.render(POST_PASSWORD_TEMPLATE);
            }
            return "common/template/" + POST_PASSWORD_TEMPLATE;
        }

        Set<PostStatus> statuses = Sets.immutableEnumSet(PostStatus.PUBLISHED);
        if (StringUtils.isNotBlank(category.getPassword())) {
            statuses = Sets.immutableEnumSet(PostStatus.INTIMATE);
        }

        CategoryDTO categoryDTO = categoryService.convertTo(category);

        final PageImpl pageable = PageImpl.of(page - 1,
                optionService.getArchivesPageSize(),
                Sort.by(DESC, "top_priority", "create_time"));
        Page<Post> postPage =
                postCategoryService.pagePostBy(category.getId(), statuses, pageable);
        Page<PostListVO> posts = postService.convertToListVo(postPage);

        // Generate meta description.
        if (StringUtils.isNotEmpty(category.getDescription())) {
            model.addAttribute("meta_description", category.getDescription());
        } else {
            model.addAttribute("meta_description", optionService.getSeoDescription());
        }

        model.addAttribute("is_category", true);
        model.addAttribute("posts", posts);
        model.addAttribute("category", categoryDTO);
        model.addAttribute("meta_keywords", optionService.getSeoKeywords());
        return themeService.render("category");
    }
}
