package com.tmser.blog.controller.admin.api;

import com.tmser.blog.model.dto.BaseCommentDTO;
import com.tmser.blog.model.entity.JournalComment;
import com.tmser.blog.model.enums.CommentStatus;
import com.tmser.blog.model.params.CommentQuery;
import com.tmser.blog.model.params.JournalCommentParam;
import com.tmser.blog.model.vo.BaseCommentVO;
import com.tmser.blog.model.vo.BaseCommentWithParentVO;
import com.tmser.blog.model.vo.JournalCommentWithJournalVO;
import com.tmser.blog.service.JournalCommentService;
import com.tmser.blog.service.OptionService;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import com.tmser.model.sort.Sort;
import com.tmser.spring.web.PageableDefault;
import com.tmser.spring.web.SortDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Journal comment controller.
 *
 * @author johnniang
 * @author guqing
 * @date 2019-04-25
 */
@RestController
@RequestMapping("/api/admin/journals/comments")
public class JournalCommentController {

    private final JournalCommentService journalCommentService;

    private final OptionService optionService;

    public JournalCommentController(JournalCommentService journalCommentService,
                                    OptionService optionService) {
        this.journalCommentService = journalCommentService;
        this.optionService = optionService;
    }

    @GetMapping
    public Page<JournalCommentWithJournalVO> pageBy(
            @PageableDefault(sort = "create_time, DESC") PageImpl pageable,
            CommentQuery commentQuery) {
        Page<JournalComment> journalCommentPage =
                journalCommentService.pageBy(commentQuery, pageable);

        return journalCommentService.convertToWithJournalVo(journalCommentPage);
    }

    @GetMapping("latest")
    public List<JournalCommentWithJournalVO> listLatest(
            @RequestParam(name = "top", defaultValue = "10") int top,
            @RequestParam(name = "status", required = false) CommentStatus status) {
        List<JournalComment> latestComments =
                journalCommentService.pageLatest(top, status).getContent();
        return journalCommentService.convertToWithJournalVo(latestComments);
    }

    @GetMapping("{journalId:\\d+}/tree_view")
    public Page<BaseCommentVO> listCommentTree(@PathVariable("journalId") Integer journalId,
                                               @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                               @SortDefault(sort = "create_time, DESC") Sort sort) {
        return journalCommentService.pageVosAllBy(journalId,
                PageImpl.of(page, optionService.getCommentPageSize(), sort));
    }

    @GetMapping("{journalId:\\d+}/list_view")
    public Page<BaseCommentWithParentVO> listComments(@PathVariable("journalId") Integer journalId,
                                                      @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                      @SortDefault(sort = "create_time, DESC") Sort sort) {
        return journalCommentService.pageWithParentVoBy(journalId,
                PageImpl.of(page, optionService.getCommentPageSize(), sort));
    }

    @PostMapping
    public BaseCommentDTO createCommentBy(@RequestBody JournalCommentParam journalCommentParam) {
        JournalComment journalComment = journalCommentService.createBy(journalCommentParam);
        return journalCommentService.convertTo(journalComment);
    }

    @PutMapping("{commentId:\\d+}/status/{status}")
    public BaseCommentDTO updateStatusBy(@PathVariable("commentId") Long commentId,
                                         @PathVariable("status") CommentStatus status) {
        // Update comment status
        JournalComment updatedJournalComment =
                journalCommentService.updateStatus(commentId, status);
        return journalCommentService.convertTo(updatedJournalComment);
    }

    @PutMapping("/{commentId:\\d+}")
    public BaseCommentDTO updateCommentBy(@PathVariable Long commentId,
                                          @RequestBody JournalCommentParam journalCommentParam) {
        JournalComment commentToUpdate = journalCommentService.getById(commentId);
        journalCommentParam.update(commentToUpdate);

        return journalCommentService.convertTo(journalCommentService.update(commentToUpdate));
    }

    @DeleteMapping("{commentId:\\d+}")
    public BaseCommentDTO deleteBy(@PathVariable("commentId") Long commentId) {
        JournalComment deletedJournalComment = journalCommentService.removeById(commentId);
        return journalCommentService.convertTo(deletedJournalComment);
    }
}
