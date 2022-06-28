package com.tmser.blog.service;

import com.tmser.blog.model.dto.LogDTO;
import com.tmser.blog.model.entity.Log;
import com.tmser.blog.service.base.CrudService;
import com.tmser.model.page.Page;

/**
 * Log service interface.
 *
 * @author johnniang
 * @date 2019-03-14
 */
public interface LogService extends CrudService<Log, Long> {

    /**
     * Lists latest logs.
     *
     * @param top top number must not be less than 0
     * @return a page of latest logs
     */
    Page<LogDTO> pageLatest(int top);
}
