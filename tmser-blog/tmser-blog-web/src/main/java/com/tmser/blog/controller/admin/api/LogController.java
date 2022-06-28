package com.tmser.blog.controller.admin.api;

import com.tmser.blog.model.dto.LogDTO;
import com.tmser.blog.model.entity.Log;
import com.tmser.blog.service.LogService;
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
@RequestMapping("/api/admin/logs")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping("latest")
    public List<LogDTO> pageLatest(@RequestParam(name = "top", defaultValue = "10") int top) {
        return logService.pageLatest(top).getContent();
    }

    @GetMapping
    public Page<LogDTO> pageBy(
            @PageableDefault(sort = "createTime, DESC") Pageable pageable) {
        Page<Log> logPage = logService.listAll(pageable);
        return logPage.convert(log -> new LogDTO().convertFrom(log));
    }

    @GetMapping("clear")
    public void clear() {
        logService.removeAll();
    }
}
