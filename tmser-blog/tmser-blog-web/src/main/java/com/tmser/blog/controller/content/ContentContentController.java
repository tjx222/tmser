package com.tmser.blog.controller.content;

import com.tmser.blog.cache.lock.CacheLock;
import com.tmser.blog.controller.content.model.*;
import com.tmser.blog.exception.NotFoundException;
import com.tmser.blog.exception.UnsupportedException;
import com.tmser.blog.model.dto.CategoryDTO;
import com.tmser.blog.model.dto.post.BasePostMinimalDTO;
import com.tmser.blog.model.entity.Post;
import com.tmser.blog.model.entity.Sheet;
import com.tmser.blog.model.enums.EncryptTypeEnum;
import com.tmser.blog.model.enums.PostPermalinkType;
import com.tmser.blog.model.enums.PostStatus;
import com.tmser.blog.model.enums.SheetPermalinkType;
import com.tmser.blog.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author ryanwang
 * @date 2020-01-07
 */
@Slf4j
@Controller
@RequestMapping
public class ContentContentController {

    private final PostModel postModel;

    private final SheetModel sheetModel;

    private final CategoryModel categoryModel;

    private final TagModel tagModel;

    private final JournalModel journalModel;

    private final PhotoModel photoModel;

    private final LinkModel linkModel;

    private final OptionService optionService;

    private final PostService postService;

    private final SheetService sheetService;

    private final AuthenticationService authenticationService;

    private final CategoryService categoryService;

    public ContentContentController(PostModel postModel,
                                    SheetModel sheetModel,
                                    CategoryModel categoryModel,
                                    TagModel tagModel,
                                    JournalModel journalModel,
                                    PhotoModel photoModel,
                                    LinkModel linkModel,
                                    OptionService optionService,
                                    PostService postService,
                                    SheetService sheetService,
                                    AuthenticationService authenticationService,
                                    CategoryService categoryService) {
        this.postModel = postModel;
        this.sheetModel = sheetModel;
        this.categoryModel = categoryModel;
        this.tagModel = tagModel;
        this.journalModel = journalModel;
        this.photoModel = photoModel;
        this.linkModel = linkModel;
        this.optionService = optionService;
        this.postService = postService;
        this.sheetService = sheetService;
        this.authenticationService = authenticationService;
        this.categoryService = categoryService;
    }

    @GetMapping("{prefix}")
    public String content(@PathVariable("prefix") String prefix,
                          @RequestParam(value = "token", required = false) String token,
                          Model model) {
        if (optionService.getArchivesPrefix().equals(prefix)) {
            return postModel.archives(1, model);
        }
        if (optionService.getCategoriesPrefix().equals(prefix)) {
            return categoryModel.list(model);
        }
        if (optionService.getTagsPrefix().equals(prefix)) {
            return tagModel.list(model);
        }
        if (optionService.getJournalsPrefix().equals(prefix)) {
            return journalModel.list(1, model);
        }
        if (optionService.getPhotosPrefix().equals(prefix)) {
            return photoModel.list(1, model);
        }
        if (optionService.getLinksPrefix().equals(prefix)) {
            return linkModel.list(model);
        }
        if (optionService.getSheetPermalinkType().equals(SheetPermalinkType.ROOT)) {
            Sheet sheet = sheetService.getBySlug(prefix);
            return sheetModel.content(sheet, token, model);
        }

        throw buildPathNotFoundException();
    }

    @GetMapping("{prefix}/page/{page:\\d+}")
    public String content(@PathVariable("prefix") String prefix,
                          @PathVariable(value = "page") Integer page,
                          HttpServletRequest request,
                          Model model) {
        if (optionService.getArchivesPrefix().equals(prefix)) {
            return postModel.archives(page, model);
        }

        if (optionService.getJournalsPrefix().equals(prefix)) {
            return journalModel.list(page, model);
        }

        if (optionService.getPhotosPrefix().equals(prefix)) {
            return photoModel.list(page, model);
        }

        throw buildPathNotFoundException();
    }

    @GetMapping("{prefix}/{slug}")
    public String content(@PathVariable("prefix") String prefix,
                          @PathVariable("slug") String slug,
                          @RequestParam(value = "token", required = false) String token,
                          Model model) {
        PostPermalinkType postPermalinkType = optionService.getPostPermalinkType();
        if (optionService.getArchivesPrefix().equals(prefix)) {
            if (postPermalinkType.equals(PostPermalinkType.DEFAULT)) {
                Post post = postService.getBySlug(slug);
                return postModel.content(post, token, model);
            }
            if (postPermalinkType.equals(PostPermalinkType.ID_SLUG)
                    && StringUtils.isNumeric(slug)) {
                Post post = postService.getById(Integer.parseInt(slug));
                return postModel.content(post, token, model);
            }
        }

        if (optionService.getCategoriesPrefix().equals(prefix)) {
            return categoryModel.listPost(model, slug, 1);
        }

        if (optionService.getTagsPrefix().equals(prefix)) {
            return tagModel.listPost(model, slug, 1);
        }

        if (postPermalinkType.equals(PostPermalinkType.YEAR) && prefix.length() == 4
                && StringUtils.isNumeric(prefix)) {
            Post post = postService.getBy(Integer.parseInt(prefix), slug);
            return postModel.content(post, token, model);
        }

        if (optionService.getSheetPermalinkType().equals(SheetPermalinkType.SECONDARY)
                && optionService.getSheetPrefix().equals(prefix)) {
            Sheet sheet = sheetService.getBySlug(slug);
            return sheetModel.content(sheet, token, model);
        }

        throw buildPathNotFoundException();
    }

    @GetMapping("{prefix}/{slug}/page/{page:\\d+}")
    public String content(@PathVariable("prefix") String prefix,
                          @PathVariable("slug") String slug,
                          @PathVariable("page") Integer page,
                          Model model) {
        if (optionService.getCategoriesPrefix().equals(prefix)) {
            return categoryModel.listPost(model, slug, page);
        }

        if (optionService.getTagsPrefix().equals(prefix)) {
            return tagModel.listPost(model, slug, page);
        }

        throw buildPathNotFoundException();
    }

    @GetMapping("{year:\\d+}/{month:\\d+}/{slug}")
    public String content(@PathVariable("year") Integer year,
                          @PathVariable("month") Integer month,
                          @PathVariable("slug") String slug,
                          @RequestParam(value = "token", required = false) String token,
                          Model model) {
        PostPermalinkType postPermalinkType = optionService.getPostPermalinkType();
        if (postPermalinkType.equals(PostPermalinkType.DATE)) {
            Post post = postService.getBy(year, month, slug);
            return postModel.content(post, token, model);
        }

        throw buildPathNotFoundException();
    }

    @GetMapping("{year:\\d+}/{month:\\d+}/{day:\\d+}/{slug}")
    public String content(@PathVariable("year") Integer year,
                          @PathVariable("month") Integer month,
                          @PathVariable("day") Integer day,
                          @PathVariable("slug") String slug,
                          @RequestParam(value = "token", required = false) String token,
                          Model model) {
        PostPermalinkType postPermalinkType = optionService.getPostPermalinkType();
        if (postPermalinkType.equals(PostPermalinkType.DAY)) {
            Post post = postService.getBy(year, month, day, slug);
            return postModel.content(post, token, model);
        }

        throw buildPathNotFoundException();
    }

    @PostMapping(value = "content/{type}/{slug:.*}/authentication")
    @CacheLock(traceRequest = true, expired = 2)
    public String password(@PathVariable("type") String type,
                           @PathVariable("slug") String slug,
                           @RequestParam(value = "password") String password) throws UnsupportedEncodingException {

        String redirectUrl;

        if (EncryptTypeEnum.POST.getName().equals(type)) {
            redirectUrl = doAuthenticationPost(slug, password);
        } else if (EncryptTypeEnum.CATEGORY.getName().equals(type)) {
            redirectUrl = doAuthenticationCategory(slug, password);
        } else {
            throw new UnsupportedException("未知的加密类型");
        }
        return "redirect:" + redirectUrl;
    }

    private NotFoundException buildPathNotFoundException() {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();

        String requestUri = "";
        if (requestAttributes instanceof ServletRequestAttributes) {
            requestUri =
                    ((ServletRequestAttributes) requestAttributes).getRequest().getRequestURI();
        }
        return new NotFoundException("无法定位到该路径：" + requestUri);
    }

    private String doAuthenticationPost(
            String slug, String password) throws UnsupportedEncodingException {
        Post post = postService.getBy(PostStatus.INTIMATE, slug);

        post.setSlug(URLEncoder.encode(post.getSlug(), StandardCharsets.UTF_8.name()));

        authenticationService.postAuthentication(post, password);

        BasePostMinimalDTO postMinimalDTO = postService.convertToMinimal(post);

        StringBuilder redirectUrl = new StringBuilder();

        if (!optionService.isEnabledAbsolutePath()) {
            redirectUrl.append(optionService.getBlogBaseUrl());
        }

        redirectUrl.append(postMinimalDTO.getFullPath());

        return redirectUrl.toString();
    }

    private String doAuthenticationCategory(String slug, String password) {
        CategoryDTO
                category = categoryService.convertTo(categoryService.getBySlugOfNonNull(slug, true));

        authenticationService.categoryAuthentication(category.getId(), password);

        StringBuilder redirectUrl = new StringBuilder();

        if (!optionService.isEnabledAbsolutePath()) {
            redirectUrl.append(optionService.getBlogBaseUrl());
        }

        redirectUrl.append(category.getFullPath());

        return redirectUrl.toString();
    }
}
