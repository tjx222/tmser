package com.tmser.blog.service;

import com.tmser.blog.model.dto.JournalWithCmtCountDTO;
import com.tmser.blog.model.entity.Journal;
import com.tmser.blog.model.enums.JournalType;
import com.tmser.blog.model.params.JournalParam;
import com.tmser.blog.model.params.JournalQuery;
import com.tmser.blog.service.base.CrudService;
import com.tmser.model.page.Page;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * Journal service interface.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-04-24
 */
public interface JournalService extends CrudService<Journal, Integer> {

    /**
     * Creates a journal.
     *
     * @param journalParam journal param must not be null
     * @return created journal
     */
    @NonNull
    Journal createBy(@NonNull JournalParam journalParam);

    /**
     * Updates a journal.
     *
     * @param journal journal must not be null
     * @return updated journal
     */
    Journal updateBy(@NonNull Journal journal);

    /**
     * Gets latest journals.
     *
     * @param top max size
     * @return latest journal page
     */
    Page<Journal> pageLatest(int top);

    /**
     * Pages journals.
     *
     * @param journalQuery journal query must not be null
     * @param pageable     page info must not be null
     * @return a page of journal
     */
    @NonNull
    Page<Journal> pageBy(@NonNull JournalQuery journalQuery, @NonNull Page pageable);

    /**
     * Lists by type.
     *
     * @param type     journal type must not be null
     * @param pageable page info must not be null
     * @return a page of journal
     */
    @NonNull
    Page<Journal> pageBy(@NonNull JournalType type, @NonNull Page pageable);

    /**
     * Converts to journal with comment count dto.
     *
     * @param journal journal must not be null
     * @return journal with comment count dto
     */
    @NonNull
    JournalWithCmtCountDTO convertTo(@NonNull Journal journal);

    /**
     * Converts to journal with comment count dto list.
     *
     * @param journals journal list
     * @return journal with comment count dto list
     */
    @NonNull
    List<JournalWithCmtCountDTO> convertToCmtCountDto(@Nullable List<Journal> journals);

    /**
     * Converts to journal with comment count dto page.
     *
     * @param journalPage journal page must not be null
     * @return a page of journal with comment count dto
     */
    @NonNull
    Page<JournalWithCmtCountDTO> convertToCmtCountDto(@NonNull Page<Journal> journalPage);

    /**
     * Increases journal likes(1).
     *
     * @param id id must not be null
     */
    void increaseLike(@NonNull Integer id);

    /**
     * Increase journal likes.
     *
     * @param likes likes must not be less than 1
     * @param id    id must not be null
     */
    void increaseLike(long likes, @NonNull Integer id);
}
