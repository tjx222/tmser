package com.tmser.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tmser.blog.exception.NotFoundException;
import com.tmser.blog.model.dto.JournalDTO;
import com.tmser.blog.model.entity.Journal;
import com.tmser.blog.model.entity.JournalComment;
import com.tmser.blog.model.params.CommentQuery;
import com.tmser.blog.model.vo.JournalCommentWithJournalVO;
import com.tmser.blog.repository.JournalCommentRepository;
import com.tmser.blog.repository.JournalRepository;
import com.tmser.blog.service.JournalCommentService;
import com.tmser.blog.service.OptionService;
import com.tmser.blog.service.UserService;
import com.tmser.blog.utils.ServiceUtils;
import com.tmser.database.mybatis.MybatisPageHelper;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import com.tmser.model.page.Pageable;
import com.tmser.model.sort.Sort;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Journal comment service implementation.
 *
 * @author johnniang
 * @date 2019-04-25
 */
@Service
public class JournalCommentServiceImpl extends BaseCommentServiceImpl<JournalComment>
        implements JournalCommentService {

    private final JournalRepository journalRepository;
    private final JournalCommentRepository journalCommentRepository;

    public JournalCommentServiceImpl(JournalCommentRepository journalCommentRepository,
                                     OptionService optionService,
                                     UserService userService,
                                     ApplicationEventPublisher eventPublisher, JournalRepository journalRepository) {
        super(journalCommentRepository, optionService, userService, eventPublisher);
        this.journalCommentRepository = journalCommentRepository;
        this.journalRepository = journalRepository;
    }

    @Override
    public void validateTarget(@NonNull Integer journalId) {
        if (journalRepository.selectById(journalId) == null) {
            throw new NotFoundException("查询不到该日志信息").setErrorData(journalId);
        }
    }

    @Override
    @NonNull
    public Page<JournalComment> pageBy(@NonNull CommentQuery commentQuery, @NonNull Page page) {
        Assert.notNull(page, "Page info must not be null");
        QueryWrapper<JournalComment> qm = new QueryWrapper<>();
        qm.eq("type", JournalComment.CT_JOUR);
        if (commentQuery.getStatus() != null) {
            qm.eq("status", commentQuery.getStatus());
        }

        if (commentQuery.getKeyword() != null) {
            String kw = StringUtils.strip(commentQuery.getKeyword());
            qm.or(w -> w.like("author", kw).like("content", kw).like("email", kw));
        }
        return MybatisPageHelper.fillPageData(journalCommentRepository.selectPage(MybatisPageHelper.changeToMybatisPage(page), qm), page);
    }

    /**
     * List All
     *
     * @return List
     */
    @Override
    public List<JournalComment> listAll() {
        return journalCommentRepository.selectList(new QueryWrapper<JournalComment>().eq(true, "type", JournalComment.CT_JOUR)
        );
    }

    /**
     * List all by sort
     *
     * @param sort sort
     * @return List
     */
    @Override
    public List<JournalComment> listAll(Sort sort) {
        Assert.notNull(sort, "Sort info must not be null");
        final QueryWrapper<JournalComment> domainQueryWrapper = new QueryWrapper<>();
        domainQueryWrapper.eq("type", JournalComment.CT_JOUR);
        sort.stream().forEach(orderItem -> {
            domainQueryWrapper.orderBy(true, orderItem.getDirection() == Sort.Direction.ASC, orderItem.getProperty());
        });
        return journalCommentRepository.selectList(domainQueryWrapper);
    }

    /**
     * List all by pageable
     *
     * @param pageable pageable
     * @return Page
     */
    @Override
    public Page<JournalComment> listAll(Pageable pageable) {
        Assert.notNull(pageable, "Pageable info must not be null");

        return MybatisPageHelper.fillPageData(
                journalCommentRepository.selectPage(MybatisPageHelper.changeToMybatisPage(pageable),
                        new QueryWrapper<JournalComment>().eq(true, "type", JournalComment.CT_JOUR)), pageable);
    }

    @Override
    @NonNull
    public List<JournalCommentWithJournalVO> convertToWithJournalVo(
            List<JournalComment> journalComments) {

        if (CollectionUtils.isEmpty(journalComments)) {
            return Collections.emptyList();
        }

        Set<Integer> journalIds =
                ServiceUtils.fetchProperty(journalComments, JournalComment::getPostId);

        // Get all journals
        List<Journal> journals = journalIds.isEmpty() ? Collections.emptyList() : journalRepository.selectBatchIds(journalIds);

        Map<Integer, Journal> journalMap = ServiceUtils.convertToMap(journals, Journal::getId);

        return journalComments.stream()
                .filter(journalComment -> journalMap.containsKey(journalComment.getPostId()))
                .map(journalComment -> {
                    JournalCommentWithJournalVO journalCmtWithJournalVo =
                            new JournalCommentWithJournalVO().convertFrom(journalComment);
                    journalCmtWithJournalVo.setJournal(
                            new JournalDTO().convertFrom(journalMap.get(journalComment.getPostId())));
                    journalCmtWithJournalVo.setAvatar(buildAvatarUrl(journalComment.getGravatarMd5()));
                    return journalCmtWithJournalVo;
                })
                .collect(Collectors.toList());
    }

    @Override
    @NonNull
    public Page<JournalCommentWithJournalVO> convertToWithJournalVo(
            @NonNull Page<JournalComment> journalCommentPage) {
        Assert.notNull(journalCommentPage, "Journal comment page must not be null");

        // Convert the list
        List<JournalCommentWithJournalVO> journalCmtWithJournalVos =
                convertToWithJournalVo(journalCommentPage.getContent());

        // Build and return
        return PageImpl.<JournalCommentWithJournalVO>of(journalCommentPage.getCurrent(), journalCommentPage.getSize(), journalCommentPage.getTotal())
                .setContent(journalCmtWithJournalVos);

    }
}
