package com.tmser.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tmser.blog.exception.BadRequestException;
import com.tmser.blog.model.dto.JournalWithCmtCountDTO;
import com.tmser.blog.model.entity.Journal;
import com.tmser.blog.model.entity.JournalComment;
import com.tmser.blog.model.enums.CommentStatus;
import com.tmser.blog.model.enums.JournalType;
import com.tmser.blog.model.params.JournalParam;
import com.tmser.blog.model.params.JournalQuery;
import com.tmser.blog.repository.JournalRepository;
import com.tmser.blog.service.JournalCommentService;
import com.tmser.blog.service.JournalService;
import com.tmser.blog.service.base.AbstractCrudService;
import com.tmser.blog.utils.MarkdownUtils;
import com.tmser.blog.utils.ServiceUtils;
import com.tmser.database.mybatis.MybatisPageHelper;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Journal service implementation.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-04-24
 */
@Slf4j
@Service
public class JournalServiceImpl extends AbstractCrudService<Journal, Integer>
        implements JournalService {

    private final JournalRepository journalRepository;

    private final JournalCommentService journalCommentService;

    public JournalServiceImpl(JournalRepository journalRepository,
                              JournalCommentService journalCommentService) {
        super(journalRepository);
        this.journalRepository = journalRepository;
        this.journalCommentService = journalCommentService;
    }

    @Override
    public Journal createBy(JournalParam journalParam) {
        Assert.notNull(journalParam, "Journal param must not be null");

        Journal journal = journalParam.convertTo();
        journal.setContent(MarkdownUtils.renderHtml(journal.getSourceContent()));

        return create(journal);
    }

    @Override
    public Journal updateBy(Journal journal) {
        Assert.notNull(journal, "Journal must not be null");

        journal.setContent(MarkdownUtils.renderHtml(journal.getSourceContent()));

        return update(journal);
    }

    @Override
    public Page<Journal> pageLatest(int top) {
        return listAll(ServiceUtils.buildLatestPageable(top));
    }

    @Override
    public Page<Journal> pageBy(JournalQuery journalQuery, Page pageable) {
        Assert.notNull(journalQuery, "Journal query must not be null");
        Assert.notNull(pageable, "Page info must not be null");
        QueryWrapper<Journal> wrapper = new QueryWrapper<>();
        if (journalQuery.getType() != null) {
            wrapper.eq("type", journalQuery.getType());
        }

        if (journalQuery.getKeyword() != null) {
            // Format like condition
            String likeCondition = StringUtils.strip(journalQuery.getKeyword());
            wrapper.like("content", likeCondition);
        }

        return MybatisPageHelper.fillPageData(
                journalRepository.selectPage(MybatisPageHelper.changeToMybatisPage(pageable), wrapper), pageable);
    }

    @Override
    public Page<Journal> pageBy(JournalType type, Page pageable) {
        Assert.notNull(type, "Journal type must not be null");
        Assert.notNull(pageable, "Page info must not be null");
        return MybatisPageHelper.fillPageData(
                journalRepository.findAllByType(type, MybatisPageHelper.changeToMybatisPage(pageable)), pageable);
    }

    @Override
    public Journal removeById(Integer id) {
        Assert.notNull(id, "Journal id must not be null");

        // Remove journal comments
        List<JournalComment> journalComments = journalCommentService.removeByPostId(id);
        log.debug("Removed journal comments: [{}]", journalComments);

        return super.removeById(id);
    }

    @Override
    public JournalWithCmtCountDTO convertTo(Journal journal) {
        Assert.notNull(journal, "Journal must not be null");

        JournalWithCmtCountDTO journalWithCmtCountDto = new JournalWithCmtCountDTO()
                .convertFrom(journal);

        journalWithCmtCountDto.setCommentCount(journalCommentService.countByStatusAndPostId(
                CommentStatus.PUBLISHED, journal.getId()));

        return journalWithCmtCountDto;
    }

    @Override
    public List<JournalWithCmtCountDTO> convertToCmtCountDto(List<Journal> journals) {
        if (CollectionUtils.isEmpty(journals)) {
            return Collections.emptyList();
        }

        // Get journal ids
        Set<Integer> journalIds = ServiceUtils.fetchProperty(journals, Journal::getId);

        // Get comment count map
        Map<Integer, Long> journalCommentCountMap =
                journalCommentService.countByStatusAndPostIds(CommentStatus.PUBLISHED, journalIds);

        return journals.stream()
                .map(journal -> {
                    JournalWithCmtCountDTO journalWithCmtCountDTO =
                            new JournalWithCmtCountDTO().convertFrom(journal);
                    // Set comment count
                    journalWithCmtCountDTO
                            .setCommentCount(journalCommentCountMap.getOrDefault(journal.getId(), 0L));
                    return journalWithCmtCountDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Page<JournalWithCmtCountDTO> convertToCmtCountDto(Page<Journal> journalPage) {
        Assert.notNull(journalPage, "Journal page must not be null");

        // Convert
        List<JournalWithCmtCountDTO> journalWithCmtCountDTOS =
                convertToCmtCountDto(journalPage.getContent());

        Page<JournalWithCmtCountDTO> pageResult = PageImpl.of(journalPage.getCurrent(), journalPage.getSize(), journalPage.getTotal());
        // Build and return
        return pageResult.setContent(journalWithCmtCountDTOS);
    }

    @Override
    @Transactional
    public void increaseLike(Integer id) {
        increaseLike(1L, id);
    }


    @Override
    @Transactional
    public void increaseLike(long likes, Integer id) {
        Assert.isTrue(likes > 0, "Likes to increase must not be less than 1");
        Assert.notNull(id, "Journal id must not be null");

        long affectedRows = journalRepository.updateLikes(likes, id);

        if (affectedRows != 1) {
            log.error("Journal with id: [{}] may not be found", id);
            throw new BadRequestException(
                    "Failed to increase likes " + likes + " for journal with id " + id);
        }
    }


}
