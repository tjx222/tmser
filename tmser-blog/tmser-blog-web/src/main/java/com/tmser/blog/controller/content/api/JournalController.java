package com.tmser.blog.controller.content.api;

import com.tmser.blog.cache.lock.CacheLock;
import com.tmser.blog.cache.lock.CacheParam;
import com.tmser.blog.model.dto.BaseCommentDTO;
import com.tmser.blog.model.dto.JournalWithCmtCountDTO;
import com.tmser.blog.model.entity.Journal;
import com.tmser.blog.model.entity.JournalComment;
import com.tmser.blog.model.enums.CommentStatus;
import com.tmser.blog.model.enums.JournalType;
import com.tmser.blog.model.params.JournalCommentParam;
import com.tmser.blog.model.vo.BaseCommentVO;
import com.tmser.blog.model.vo.BaseCommentWithParentVO;
import com.tmser.blog.model.vo.CommentWithHasChildrenVO;
import com.tmser.blog.service.JournalCommentService;
import com.tmser.blog.service.JournalService;
import com.tmser.blog.service.OptionService;
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
 * Content journal controller.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-04-26
 */
@RestController("ApiContentJournalController")
@RequestMapping("/api/content/journals")
public class JournalController {

    private final JournalService journalService;

    private final JournalCommentService journalCommentService;

    private final OptionService optionService;

    public JournalController(JournalService journalService,
                             JournalCommentService journalCommentService,
                             OptionService optionService) {
        this.journalService = journalService;
        this.journalCommentService = journalCommentService;
        this.optionService = optionService;
    }

    @GetMapping
    public Page<JournalWithCmtCountDTO> pageBy(
            @PageableDefault(sort = "createTime,DESC") PageImpl pageable) {
        Page<Journal> journals = journalService.pageBy(JournalType.PUBLIC, pageable);
        return journalService.convertToCmtCountDto(journals);
    }

    @GetMapping("{journalId:\\d+}")
    public JournalWithCmtCountDTO getBy(@PathVariable("journalId") Integer journalId) {
        Journal journal = journalService.getById(journalId);
        return journalService.convertTo(journal);
    }

    @GetMapping("{journalId:\\d+}/comments/top_view")
    public Page<CommentWithHasChildrenVO> listTopComments(
            @PathVariable("journalId") Integer journalId,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @SortDefault(sort = "createTime, DESC") Sort sort) {
        return journalCommentService.pageTopCommentsBy(journalId, CommentStatus.PUBLISHED,
                PageImpl.of(page, optionService.getCommentPageSize(), sort));
    }

    @GetMapping("{journalId:\\d+}/comments/{commentParentId:\\d+}/children")
    public List<BaseCommentDTO> listChildrenBy(@PathVariable("journalId") Integer journalId,
                                               @PathVariable("commentParentId") Long commentParentId,
                                               @SortDefault(sort = "createTime, DESC") Sort sort) {
        // Find all children comments
        List<JournalComment> postComments = journalCommentService
                .listChildrenBy(journalId, commentParentId, CommentStatus.PUBLISHED, sort);
        // Convert to base comment dto
        return journalCommentService.convertTo(postComments);
    }

    @GetMapping("{journalId:\\d+}/comments/tree_view")
    public Page<BaseCommentVO> listCommentsTree(@PathVariable("journalId") Integer journalId,
                                                @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                @SortDefault(sort = "createTime,DESC") Sort sort) {
        return journalCommentService
                .pageVosBy(journalId, PageImpl.of(page, optionService.getCommentPageSize(), sort));
    }

    @GetMapping("{journalId:\\d+}/comments/list_view")
    public Page<BaseCommentWithParentVO> listComments(@PathVariable("journalId") Integer journalId,
                                                      @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                      @SortDefault(sort = "createTime,DESC") Sort sort) {
        return journalCommentService.pageWithParentVoBy(journalId,
                PageImpl.of(page, optionService.getCommentPageSize(), sort));
    }

    @PostMapping("comments")
    @CacheLock(autoDelete = false, traceRequest = true)
    public BaseCommentDTO comment(@RequestBody JournalCommentParam journalCommentParam) {

        // Escape content
        journalCommentParam.setContent(HtmlUtils
                .htmlEscape(journalCommentParam.getContent(), StandardCharsets.UTF_8.displayName()));
        return journalCommentService.convertTo(journalCommentService.createBy(journalCommentParam));
    }

    @PostMapping("{id:\\d+}/likes")
    @CacheLock(autoDelete = false, traceRequest = true)
    public void like(@PathVariable("id") @CacheParam Integer id) {
        journalService.increaseLike(id);
    }
}
