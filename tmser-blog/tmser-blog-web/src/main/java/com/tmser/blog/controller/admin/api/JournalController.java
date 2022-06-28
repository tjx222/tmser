package com.tmser.blog.controller.admin.api;

import com.tmser.blog.model.dto.JournalDTO;
import com.tmser.blog.model.dto.JournalWithCmtCountDTO;
import com.tmser.blog.model.entity.Journal;
import com.tmser.blog.model.params.JournalParam;
import com.tmser.blog.model.params.JournalQuery;
import com.tmser.blog.service.JournalService;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import com.tmser.spring.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Journal controller.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-04-25
 */
@RestController
@RequestMapping("/api/admin/journals")
public class JournalController {

    private final JournalService journalService;

    public JournalController(JournalService journalService) {
        this.journalService = journalService;
    }

    @GetMapping
    public Page<JournalWithCmtCountDTO> pageBy(
            @PageableDefault(sort = "createTime,DESC") PageImpl pageable,
            JournalQuery journalQuery) {
        Page<Journal> journalPage = journalService.pageBy(journalQuery, pageable);
        return journalService.convertToCmtCountDto(journalPage);
    }

    @GetMapping("latest")
    public List<JournalWithCmtCountDTO> pageLatest(
            @RequestParam(name = "top", defaultValue = "10") int top) {
        List<Journal> journals = journalService.pageLatest(top).getContent();
        return journalService.convertToCmtCountDto(journals);
    }

    @PostMapping
    public JournalDTO createBy(@RequestBody @Valid JournalParam journalParam) {
        Journal createdJournal = journalService.createBy(journalParam);
        return journalService.convertTo(createdJournal);
    }

    @PutMapping("{id:\\d+}")
    public JournalDTO updateBy(@PathVariable("id") Integer id,
                               @RequestBody @Valid JournalParam journalParam) {
        Journal journal = journalService.getById(id);
        journalParam.update(journal);
        Journal updatedJournal = journalService.updateBy(journal);
        return journalService.convertTo(updatedJournal);
    }

    @DeleteMapping("{journalId:\\d+}")
    public JournalDTO deleteBy(@PathVariable("journalId") Integer journalId) {
        Journal deletedJournal = journalService.removeById(journalId);
        return journalService.convertTo(deletedJournal);
    }
}
