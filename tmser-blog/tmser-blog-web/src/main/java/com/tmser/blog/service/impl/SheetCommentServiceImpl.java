package com.tmser.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tmser.blog.exception.BadRequestException;
import com.tmser.blog.exception.NotFoundException;
import com.tmser.blog.model.dto.post.BasePostMinimalDTO;
import com.tmser.blog.model.entity.SheetComment;
import com.tmser.blog.model.entity.Sheet;
import com.tmser.blog.model.entity.SheetComment;
import com.tmser.blog.model.enums.SheetPermalinkType;
import com.tmser.blog.model.params.CommentQuery;
import com.tmser.blog.model.vo.SheetCommentWithSheetVO;
import com.tmser.blog.repository.SheetCommentRepository;
import com.tmser.blog.repository.SheetRepository;
import com.tmser.blog.service.OptionService;
import com.tmser.blog.service.SheetCommentService;
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

import java.util.*;
import java.util.stream.Collectors;

import static com.tmser.blog.model.support.HaloConst.URL_SEPARATOR;

/**
 * Sheet comment service implementation.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-04-24
 */
@Service
public class SheetCommentServiceImpl extends BaseCommentServiceImpl<SheetComment>
        implements SheetCommentService {

    private final SheetRepository sheetRepository;
    
    private final SheetCommentRepository sheetCommentRepository;

    public SheetCommentServiceImpl(SheetCommentRepository sheetCommentRepository,
                                   OptionService optionService,
                                   UserService userService,
                                   ApplicationEventPublisher eventPublisher,
                                   SheetRepository sheetRepository) {
        super(sheetCommentRepository, optionService, userService, eventPublisher);
        this.sheetCommentRepository = sheetCommentRepository;
        this.sheetRepository = sheetRepository;
    }

    @Override
    public void validateTarget(@NonNull Integer sheetId) {
        Sheet sheet = Optional.ofNullable(sheetRepository.selectById(sheetId))
                .orElseThrow(() -> new NotFoundException("查询不到该页面的信息").setErrorData(sheetId));

        if (sheet.getDisallowComment()) {
            throw new BadRequestException("该页面已被禁止评论").setErrorData(sheetId);
        }
    }

    @Override
    @NonNull
    public SheetCommentWithSheetVO convertToWithSheetVo(@NonNull SheetComment comment) {
        Assert.notNull(comment, "SheetComment must not be null");
        SheetCommentWithSheetVO sheetCommentWithSheetVo =
                new SheetCommentWithSheetVO().convertFrom(comment);

        BasePostMinimalDTO basePostMinimalDto =
                new BasePostMinimalDTO().convertFrom(sheetRepository.selectById(comment.getPostId()));

        sheetCommentWithSheetVo.setSheet(buildSheetFullPath(basePostMinimalDto));

        sheetCommentWithSheetVo.setAvatar(buildAvatarUrl(comment.getGravatarMd5()));

        return sheetCommentWithSheetVo;
    }


    @Override
    @NonNull
    public Page<SheetComment> pageBy(@NonNull CommentQuery commentQuery, @NonNull Page page) {
        Assert.notNull(page, "Page info must not be null");
        QueryWrapper<SheetComment> qm = new QueryWrapper<>();
        qm.eq("type", SheetComment.CT_SHEET);
        if (commentQuery.getStatus() != null) {
            qm.eq("status", commentQuery.getStatus());
        }

        if (commentQuery.getKeyword() != null) {
            String kw = StringUtils.strip(commentQuery.getKeyword());
            qm.or(w -> w.like("author", kw).like("content", kw).like("email", kw));
        }
        return MybatisPageHelper.fillPageData(sheetCommentRepository.selectPage(MybatisPageHelper.changeToMybatisPage(page), qm), page);
    }

    /**
     * List All
     *
     * @return List
     */
    @Override
    public List<SheetComment> listAll() {
        return sheetCommentRepository.selectList(
                new QueryWrapper<SheetComment>().eq(true, "type", SheetComment.CT_SHEET)
        );
    }

    /**
     * List all by sort
     *
     * @param sort sort
     * @return List
     */
    @Override
    public List<SheetComment> listAll(Sort sort) {
        Assert.notNull(sort, "Sort info must not be null");
        final QueryWrapper<SheetComment> domainQueryWrapper = new QueryWrapper<>();
        domainQueryWrapper.eq("type", SheetComment.CT_JOUR);
        sort.stream().forEach(orderItem -> {
            domainQueryWrapper.orderBy(true, orderItem.getDirection() == Sort.Direction.ASC, orderItem.getProperty());
        });
        return sheetCommentRepository.selectList(domainQueryWrapper);
    }

    /**
     * List all by pageable
     *
     * @param pageable pageable
     * @return Page
     */
    @Override
    public Page<SheetComment> listAll(Pageable pageable) {
        Assert.notNull(pageable, "Pageable info must not be null");

        return MybatisPageHelper.fillPageData(
                sheetCommentRepository.selectPage(MybatisPageHelper.changeToMybatisPage(pageable),
                        new QueryWrapper<SheetComment>().eq(true, "type", SheetComment.CT_SHEET)), pageable);
    }


    @Override
    @NonNull
    public List<SheetCommentWithSheetVO> convertToWithSheetVo(List<SheetComment> sheetComments) {
        if (CollectionUtils.isEmpty(sheetComments)) {
            return Collections.emptyList();
        }

        Set<Integer> sheetIds = ServiceUtils.fetchProperty(sheetComments, SheetComment::getPostId);

        Map<Integer, Sheet> sheetMap = sheetIds.isEmpty() ? Collections.emptyMap() :
                ServiceUtils.convertToMap(sheetRepository.selectBatchIds(sheetIds), Sheet::getId);

        return sheetComments.stream()
                .filter(comment -> sheetMap.containsKey(comment.getPostId()))
                .map(comment -> {
                    SheetCommentWithSheetVO sheetCmtWithPostVo =
                            new SheetCommentWithSheetVO().convertFrom(comment);

                    BasePostMinimalDTO postMinimalDto =
                            new BasePostMinimalDTO().convertFrom(sheetMap.get(comment.getPostId()));

                    sheetCmtWithPostVo.setSheet(buildSheetFullPath(postMinimalDto));

                    sheetCmtWithPostVo.setAvatar(buildAvatarUrl(comment.getGravatarMd5()));

                    return sheetCmtWithPostVo;
                })
                .collect(Collectors.toList());
    }

    @Override
    @NonNull
    public Page<SheetCommentWithSheetVO> convertToWithSheetVo(
            @NonNull Page<SheetComment> sheetCommentPage) {
        Assert.notNull(sheetCommentPage, "Sheet comment page must not be null");
        Page<SheetCommentWithSheetVO> pageResult = PageImpl.of(sheetCommentPage.getCurrent(), sheetCommentPage.getSize(), sheetCommentPage.getTotal());
        return pageResult.setContent(convertToWithSheetVo(sheetCommentPage.getContent()));
    }

    private BasePostMinimalDTO buildSheetFullPath(BasePostMinimalDTO basePostMinimalDto) {
        StringBuilder fullPath = new StringBuilder();

        SheetPermalinkType permalinkType = optionService.getSheetPermalinkType();

        if (optionService.isEnabledAbsolutePath()) {
            fullPath.append(optionService.getBlogBaseUrl());
        }

        if (permalinkType.equals(SheetPermalinkType.SECONDARY)) {
            fullPath.append(URL_SEPARATOR)
                    .append(optionService.getSheetPrefix())
                    .append(URL_SEPARATOR)
                    .append(basePostMinimalDto.getSlug())
                    .append(optionService.getPathSuffix());
        } else if (permalinkType.equals(SheetPermalinkType.ROOT)) {
            fullPath.append(URL_SEPARATOR)
                    .append(basePostMinimalDto.getSlug())
                    .append(optionService.getPathSuffix());
        }

        basePostMinimalDto.setFullPath(fullPath.toString());
        return basePostMinimalDto;
    }

}
