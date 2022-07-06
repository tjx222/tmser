package com.tmser.blog.service.impl;

import com.tmser.blog.model.dto.LogDTO;
import com.tmser.blog.model.entity.Log;
import com.tmser.blog.repository.LogRepository;
import com.tmser.blog.service.LogService;
import com.tmser.blog.service.base.AbstractCrudService;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import com.tmser.model.sort.Sort;
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
public class LogServiceImpl extends AbstractCrudService<Log, Long> implements LogService {

    private final LogRepository logRepository;

    public LogServiceImpl(LogRepository logRepository) {
        super(logRepository);
        this.logRepository = logRepository;
    }

    @Override
    public Page<LogDTO> pageLatest(int top) {
        Assert.isTrue(top > 0, "Top number must not be less than 0");

        // Build page request
        Page<Log> latestPageable = PageImpl.of(0, top);
        Page<LogDTO> resultPage = PageImpl.of(0, top, Sort.by(Sort.Direction.DESC, "create_time"));
        // List all
        return resultPage.setContent(listAll(latestPageable).getContent()
                        .stream().map(log -> new LogDTO().<LogDTO>convertFrom(log)).collect(Collectors.toList()))
                .setTotal(latestPageable.getTotal());
    }
}
