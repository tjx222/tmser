package com.tmser.blog.service.impl;

import com.tmser.blog.model.dto.OptionSimpleDTO;
import com.tmser.blog.model.params.OptionQuery;
import com.tmser.blog.service.ClientOptionService;
import com.tmser.blog.service.OptionService;
import com.tmser.model.page.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Map;

import static java.util.stream.Collectors.toMap;


/**
 * The client only provides filtered data
 *
 * @author LIlGG
 * @date 2021/8/2
 */
@Service
public class ClientOptionServiceImpl implements ClientOptionService {

    private final OptionService optionService;

    private final OptionFilter optionFilter;

    ClientOptionServiceImpl(OptionService optionService) {
        this.optionService = optionService;
        this.optionFilter = new OptionFilter(optionService);
    }

    @Override
    @Transactional
    public Map<String, Object> listOptions() {
        Map<String, Object> options = optionService.listOptions();
        return optionFilter.filter(options.keySet()).parallelStream()
                .collect(toMap(optionName -> optionName, options::get));
    }

    @Override
    public Page<OptionSimpleDTO> pageDtosBy(Page pageable, OptionQuery optionQuery) {
        return optionService.pageDtosBy(pageable, optionQuery);
    }

    @Override
    public int getPostPageSize() {
        return optionService.getPostPageSize();
    }

    @Override
    public int getArchivesPageSize() {
        return optionService.getArchivesPageSize();
    }

    @Override
    public int getCommentPageSize() {
        return optionService.getCommentPageSize();
    }

    @Override
    public int getRssPageSize() {
        return optionService.getRssPageSize();
    }

    @Override
    public Locale getLocale() {
        return optionService.getLocale();
    }

    @Override
    public String getBlogBaseUrl() {
        return optionService.getBlogBaseUrl();
    }

    @Override
    public long getBirthday() {
        return optionService.getBirthday();
    }

}
