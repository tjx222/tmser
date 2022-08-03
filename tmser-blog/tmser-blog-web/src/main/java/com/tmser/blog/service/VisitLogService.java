package com.tmser.blog.service;

import com.tmser.blog.model.dto.LogDTO;
import com.tmser.blog.model.dto.VisitLogDTO;
import com.tmser.blog.model.entity.Log;
import com.tmser.blog.model.entity.VisitLog;
import com.tmser.blog.model.params.VisitLogParam;
import com.tmser.blog.service.base.CrudService;
import com.tmser.model.page.Page;
import com.tmser.model.page.Pageable;
import com.tmser.spring.web.PageableDefault;

/**
 * Log service interface.
 *
 * @author johnniang
 * @date 2019-03-14
 */
public interface VisitLogService extends CrudService<VisitLog, Integer> {

    /**
     * Lists logs.
     *
     */
    Page<VisitLogDTO> pageList(VisitLogParam visitLogParam, Pageable pageable);
}
