package com.tmser.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tmser.blog.model.dto.LogDTO;
import com.tmser.blog.model.dto.VisitLogDTO;
import com.tmser.blog.model.entity.Log;
import com.tmser.blog.model.entity.VisitLog;
import com.tmser.blog.model.params.VisitLogParam;
import com.tmser.blog.repository.LogRepository;
import com.tmser.blog.repository.VisitLogRepository;
import com.tmser.blog.service.LogService;
import com.tmser.blog.service.VisitLogService;
import com.tmser.blog.service.base.AbstractCrudService;
import com.tmser.database.mybatis.MybatisPageHelper;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import com.tmser.model.page.Pageable;
import com.tmser.model.sort.Sort;
import com.tmser.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.stream.Collectors;

/**
 * LogService implementation class
 *
 * @author ryanwang
 * @date 2019-03-14
 */
@Service
public class VisitLogServiceImpl extends AbstractCrudService<VisitLog, Integer> implements VisitLogService {

    private final VisitLogRepository logRepository;

    public VisitLogServiceImpl(VisitLogRepository logRepository) {
        super(logRepository);
        this.logRepository = logRepository;
    }

    @Override
    public Page<VisitLogDTO> pageList(VisitLogParam visitLogParam, Pageable pageable) {
        // Build page request
        Page<VisitLogDTO> resultPage = PageImpl.of(0, pageable.getSize(), Sort.by(Sort.Direction.DESC, "create_time"));
        // List all
        VisitLog visitLog = new VisitLog();
        visitLog.setContentId(visitLogParam.getContentId());
        visitLog.setShareId(visitLogParam.getShareId());
        visitLog.setIpAddress(visitLogParam.getIpAddress());
        QueryWrapper<VisitLog> visitLogLambdaQueryWrapper = Wrappers.query(visitLog);

        visitLogLambdaQueryWrapper.likeRight(
                StringUtils.isNotEmpty(visitLogParam.getContentName()), "content_name", visitLogParam.getContentName());

        visitLogLambdaQueryWrapper.likeRight(
                StringUtils.isNotEmpty(visitLogParam.getShareName()), "share_name", visitLogParam.getShareName());

        IPage<VisitLog> visitLogIPage = logRepository.selectPage(
                MybatisPageHelper.changeToMybatisPage(pageable), visitLogLambdaQueryWrapper);
        return resultPage.setContent(visitLogIPage.getRecords()
                        .stream().map(log -> new VisitLogDTO().<VisitLogDTO>convertFrom(log)).collect(Collectors.toList()))
                .setTotal(visitLogIPage.getTotal());
    }
}
