package com.tmser.blog.controller.admin.api;

import com.tmser.blog.model.dto.LogDTO;
import com.tmser.blog.model.dto.VisitLogDTO;
import com.tmser.blog.model.entity.Log;
import com.tmser.blog.model.entity.VisitLog;
import com.tmser.blog.model.params.VisitLogParam;
import com.tmser.blog.service.LogService;
import com.tmser.blog.service.VisitLogService;
import com.tmser.model.page.Page;
import com.tmser.model.page.Pageable;
import com.tmser.spring.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Log controller.
 *
 * @author johnniang
 * @date 2019-03-19
 */
@RestController
@RequestMapping("/api/admin/visitLogs")
public class VisitLogController {

    private final VisitLogService visitLogService;

    public VisitLogController(VisitLogService visitLogService) {
        this.visitLogService = visitLogService;
    }

    @GetMapping
    public Page<VisitLogDTO> pageBy(VisitLogParam visitLogParam,
                                    @PageableDefault(sort = "create_time, DESC") Pageable pageable) {
        return visitLogService.pageList(visitLogParam, pageable);
    }

}
