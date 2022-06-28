package com.tmser.blog.service;

import com.tmser.blog.model.entity.JournalComment;
import com.tmser.blog.model.vo.JournalCommentWithJournalVO;
import com.tmser.blog.service.base.BaseCommentService;
import com.tmser.model.page.Page;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * Journal comment service interface.
 *
 * @author johnniang
 * @date 2019-04-25
 */
public interface JournalCommentService extends BaseCommentService<JournalComment> {

    @NonNull
    List<JournalCommentWithJournalVO> convertToWithJournalVo(
            @Nullable List<JournalComment> journalComments);

    @NonNull
    Page<JournalCommentWithJournalVO> convertToWithJournalVo(
            @NonNull Page<JournalComment> journalCommentPage);
}
