package com.tmser.blog.controller.content.api;

import com.tmser.blog.cache.lock.CacheLock;
import com.tmser.blog.model.dto.BaseCommentDTO;
import com.tmser.blog.model.entity.Sheet;
import com.tmser.blog.model.entity.SheetComment;
import com.tmser.blog.model.enums.CommentStatus;
import com.tmser.blog.model.enums.PostStatus;
import com.tmser.blog.model.params.SheetCommentParam;
import com.tmser.blog.model.vo.*;
import com.tmser.blog.service.OptionService;
import com.tmser.blog.service.SheetCommentService;
import com.tmser.blog.service.SheetService;
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
 * Content sheet controller.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-04-26
 */
@RestController("ApiContentSheetController")
@RequestMapping("/api/content/sheets")
public class SheetController {

    private final SheetService sheetService;

    private final SheetCommentService sheetCommentService;

    private final OptionService optionService;

    public SheetController(SheetService sheetService, SheetCommentService sheetCommentService,
                           OptionService optionService) {
        this.sheetService = sheetService;
        this.sheetCommentService = sheetCommentService;
        this.optionService = optionService;
    }

    @GetMapping
    public Page<SheetListVO> pageBy(
            @PageableDefault(sort = "createTime,DESC") PageImpl pageable) {
        Page<Sheet> sheetPage = sheetService.pageBy(PostStatus.PUBLISHED, pageable);
        return sheetService.convertToListVo(sheetPage);
    }

    @GetMapping("{sheetId:\\d+}")
    public SheetDetailVO getBy(@PathVariable("sheetId") Integer sheetId,
                               @RequestParam(value = "formatDisabled", required = false, defaultValue = "true")
                                       Boolean formatDisabled,
                               @RequestParam(value = "sourceDisabled", required = false, defaultValue = "false")
                                       Boolean sourceDisabled) {
        SheetDetailVO sheetDetailVO = sheetService.convertToDetailVo(sheetService.getById(sheetId));

        if (formatDisabled) {
            // Clear the format content
            sheetDetailVO.setContent(null);
        }

        if (sourceDisabled) {
            // Clear the original content
            sheetDetailVO.setOriginalContent(null);
        }

        sheetService.publishVisitEvent(sheetDetailVO.getId());

        return sheetDetailVO;
    }

    @GetMapping("/slug")
    public SheetDetailVO getBy(@RequestParam("slug") String slug,
                               @RequestParam(value = "formatDisabled", required = false, defaultValue = "true")
                                       Boolean formatDisabled,
                               @RequestParam(value = "sourceDisabled", required = false, defaultValue = "false")
                                       Boolean sourceDisabled) {
        SheetDetailVO sheetDetailVO = sheetService.convertToDetailVo(sheetService.getBySlug(slug));

        if (formatDisabled) {
            // Clear the format content
            sheetDetailVO.setContent(null);
        }

        if (sourceDisabled) {
            // Clear the original content
            sheetDetailVO.setOriginalContent(null);
        }

        sheetService.publishVisitEvent(sheetDetailVO.getId());

        return sheetDetailVO;
    }

    @GetMapping("{sheetId:\\d+}/comments/top_view")
    public Page<CommentWithHasChildrenVO> listTopComments(@PathVariable("sheetId") Integer sheetId,
                                                          @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                          @SortDefault(sort = "createTime,DESC") Sort sort) {
        return sheetCommentService.pageTopCommentsBy(sheetId, CommentStatus.PUBLISHED,
                PageImpl.of(page, optionService.getCommentPageSize(), sort));
    }

    @GetMapping("{sheetId:\\d+}/comments/{commentParentId:\\d+}/children")
    public List<BaseCommentDTO> listChildrenBy(@PathVariable("sheetId") Integer sheetId,
                                               @PathVariable("commentParentId") Long commentParentId,
                                               @SortDefault(sort = "createTime, DESC") Sort sort) {
        // Find all children comments
        List<SheetComment> sheetComments = sheetCommentService
                .listChildrenBy(sheetId, commentParentId, CommentStatus.PUBLISHED, sort);
        // Convert to base comment dto
        return sheetCommentService.convertTo(sheetComments);
    }


    @GetMapping("{sheetId:\\d+}/comments/tree_view")
    public Page<BaseCommentVO> listCommentsTree(@PathVariable("sheetId") Integer sheetId,
                                                @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                @SortDefault(sort = "createTime, DESC") Sort sort) {
        return sheetCommentService
                .pageVosBy(sheetId, PageImpl.of(page, optionService.getCommentPageSize(), sort));
    }

    @GetMapping("{sheetId:\\d+}/comments/list_view")
    public Page<BaseCommentWithParentVO> listComments(@PathVariable("sheetId") Integer sheetId,
                                                      @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                      @SortDefault(sort = "createTime,DESC") Sort sort) {
        return sheetCommentService.pageWithParentVoBy(sheetId,
                PageImpl.of(page, optionService.getCommentPageSize(), sort));
    }

    @PostMapping("comments")
    @CacheLock(autoDelete = false, traceRequest = true)
    public BaseCommentDTO comment(@RequestBody SheetCommentParam sheetCommentParam) {

        // Escape content
        sheetCommentParam.setContent(HtmlUtils
                .htmlEscape(sheetCommentParam.getContent(), StandardCharsets.UTF_8.displayName()));
        return sheetCommentService.convertTo(sheetCommentService.createBy(sheetCommentParam));
    }
}
