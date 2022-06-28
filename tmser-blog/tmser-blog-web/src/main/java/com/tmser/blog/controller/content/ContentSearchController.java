package com.tmser.blog.controller.content;

import com.tmser.blog.model.entity.Post;
import com.tmser.blog.model.vo.PostListVO;
import com.tmser.blog.service.OptionService;
import com.tmser.blog.service.PostService;
import com.tmser.blog.service.ThemeService;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import com.tmser.model.sort.Sort;
import com.tmser.spring.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;

import static com.tmser.model.sort.Sort.Direction.DESC;

/**
 * Search controller.
 *
 * @author ryanwang
 * @date 2019-04-21
 */
@Controller
@RequestMapping(value = "/search")
public class ContentSearchController {

    private final PostService postService;

    private final OptionService optionService;

    private final ThemeService themeService;

    public ContentSearchController(PostService postService, OptionService optionService,
                                   ThemeService themeService) {
        this.postService = postService;
        this.optionService = optionService;
        this.themeService = themeService;
    }

    /**
     * Render post search page.
     *
     * @param model   model
     * @param keyword keyword
     * @return template path : themes/{theme}/search.ftl
     */
    @GetMapping
    public String search(Model model,
                         @RequestParam(value = "keyword") String keyword) {
        return this.search(model, HtmlUtils.htmlEscape(keyword), 1, Sort.by(DESC, "createTime"));
    }

    /**
     * Render post search page.
     *
     * @param model   model
     * @param keyword keyword
     * @return template path :themes/{theme}/search.ftl
     */
    @GetMapping(value = "page/{page}")
    public String search(Model model,
                         @RequestParam(value = "keyword") String keyword,
                         @PathVariable(value = "page") Integer page,
                         @SortDefault(sort = "createTime,desc") Sort sort) {
        final Page pageable = PageImpl.of(page - 1, optionService.getPostPageSize(), sort);
        final Page<Post> postPage = postService.pageBy(keyword, pageable);

        final Page<PostListVO> posts = postService.convertToListVo(postPage);

        model.addAttribute("is_search", true);
        model.addAttribute("keyword", HtmlUtils.htmlEscape(keyword));
        model.addAttribute("posts", posts);
        model.addAttribute("meta_keywords", optionService.getSeoKeywords());
        model.addAttribute("meta_description", optionService.getSeoDescription());
        return themeService.render("search");
    }
}
