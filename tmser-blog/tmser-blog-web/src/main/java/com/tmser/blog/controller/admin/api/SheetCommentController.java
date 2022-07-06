package com.tmser.blog.controller.admin.api;

import com.tmser.blog.model.dto.BaseCommentDTO;
import com.tmser.blog.model.entity.SheetComment;
import com.tmser.blog.model.enums.CommentStatus;
import com.tmser.blog.model.params.CommentQuery;
import com.tmser.blog.model.params.SheetCommentParam;
import com.tmser.blog.model.vo.BaseCommentVO;
import com.tmser.blog.model.vo.BaseCommentWithParentVO;
import com.tmser.blog.model.vo.SheetCommentWithSheetVO;
import com.tmser.blog.service.OptionService;
import com.tmser.blog.service.SheetCommentService;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import com.tmser.model.sort.Sort;
import com.tmser.spring.web.PageableDefault;
import com.tmser.spring.web.SortDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Sheet comment controller.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-04-25
 */
@RestController
@RequestMapping("/api/admin/sheets/comments")
public class SheetCommentController {

    private final SheetCommentService sheetCommentService;

    private final OptionService optionService;

    public SheetCommentController(SheetCommentService sheetCommentService,
                                  OptionService optionService) {
        this.sheetCommentService = sheetCommentService;
        this.optionService = optionService;
    }

    @GetMapping
    public Page<SheetCommentWithSheetVO> pageBy(
            @PageableDefault(sort = "create_time,desc") PageImpl pageable,
            CommentQuery commentQuery) {
        Page<SheetComment> sheetCommentPage = sheetCommentService.pageBy(commentQuery, pageable);
        return sheetCommentService.convertToWithSheetVo(sheetCommentPage);
    }

    @GetMapping("latest")
    public List<SheetCommentWithSheetVO> listLatest(
            @RequestParam(name = "top", defaultValue = "10") int top,
            @RequestParam(name = "status", required = false) CommentStatus status) {
        Page<SheetComment> sheetCommentPage = sheetCommentService.pageLatest(top, status);
        return sheetCommentService.convertToWithSheetVo(sheetCommentPage.getContent());
    }

    @GetMapping("{sheetId:\\d+}/tree_view")
    public Page<BaseCommentVO> listCommentTree(@PathVariable("sheetId") Integer sheetId,
                                               @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                               @SortDefault(sort = "create_time,DESC") Sort sort) {
        return sheetCommentService
                .pageVosAllBy(sheetId, PageImpl.of(page, optionService.getCommentPageSize(), sort));
    }

    @GetMapping("{sheetId:\\d+}/list_view")
    public Page<BaseCommentWithParentVO> listComments(@PathVariable("sheetId") Integer sheetId,
                                                      @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                      @SortDefault(sort = "create_time, DESC") Sort sort) {
        return sheetCommentService.pageWithParentVoBy(sheetId,
                PageImpl.of(page, optionService.getCommentPageSize(), sort));
    }

    @PostMapping
    public BaseCommentDTO createBy(@RequestBody SheetCommentParam commentParam) {
        SheetComment createdComment = sheetCommentService.createBy(commentParam);
        return sheetCommentService.convertTo(createdComment);
    }

    @PutMapping("{commentId:\\d+}/status/{status}")
    public BaseCommentDTO updateStatusBy(@PathVariable("commentId") Long commentId,
                                         @PathVariable("status") CommentStatus status) {
        // Update comment status
        SheetComment updatedSheetComment = sheetCommentService.updateStatus(commentId, status);
        return sheetCommentService.convertTo(updatedSheetComment);
    }

    @PutMapping("status/{status}")
    public List<BaseCommentDTO> updateStatusInBatch(
            @PathVariable(name = "status") CommentStatus status,
            @RequestBody List<Long> ids) {
        List<SheetComment> comments = sheetCommentService.updateStatusByIds(ids, status);
        return sheetCommentService.convertTo(comments);
    }


    @DeleteMapping("{commentId:\\d+}")
    public BaseCommentDTO deletePermanently(@PathVariable("commentId") Long commentId) {
        SheetComment deletedSheetComment = sheetCommentService.removeById(commentId);
        return sheetCommentService.convertTo(deletedSheetComment);
    }

    @DeleteMapping
    public List<SheetComment> deletePermanentlyInBatch(@RequestBody List<Long> ids) {
        return sheetCommentService.removeByIds(ids);
    }

    @GetMapping("{commentId:\\d+}")
    public SheetCommentWithSheetVO getBy(@PathVariable("commentId") Long commentId) {
        SheetComment comment = sheetCommentService.getById(commentId);
        return sheetCommentService.convertToWithSheetVo(comment);
    }

    @PutMapping("{commentId:\\d+}")
    public BaseCommentDTO updateBy(@Valid @RequestBody SheetCommentParam commentParam,
                                   @PathVariable("commentId") Long commentId) {
        SheetComment commentToUpdate = sheetCommentService.getById(commentId);

        commentParam.update(commentToUpdate);

        return sheetCommentService.convertTo(sheetCommentService.update(commentToUpdate));
    }
}
