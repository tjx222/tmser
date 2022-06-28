package com.tmser.blog.service;

import com.tmser.blog.model.entity.SheetComment;
import com.tmser.blog.model.vo.SheetCommentWithSheetVO;
import com.tmser.blog.service.base.BaseCommentService;
import com.tmser.model.page.Page;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * Sheet comment service interface.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-04-24
 */
public interface SheetCommentService extends BaseCommentService<SheetComment> {

    /**
     * Converts to with sheet vo
     *
     * @param comment comment
     * @return a comment with sheet vo
     */
    @NonNull
    SheetCommentWithSheetVO convertToWithSheetVo(@NonNull SheetComment comment);

    /**
     * Converts to with sheet vo
     *
     * @param sheetComments sheet comments
     * @return a sheet comments with sheet vo
     */
    @NonNull
    List<SheetCommentWithSheetVO> convertToWithSheetVo(@Nullable List<SheetComment> sheetComments);

    /**
     * Converts to with sheet vo
     *
     * @param sheetCommentPage sheet comments
     * @return a page of sheet comments with sheet vo
     */
    @NonNull
    Page<SheetCommentWithSheetVO> convertToWithSheetVo(
            @NonNull Page<SheetComment> sheetCommentPage);
}
