package com.tmser.blog.controller.admin.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tmser.blog.model.dto.StatisticDTO;
import com.tmser.blog.model.dto.StatisticWithUserDTO;
import com.tmser.blog.service.StatisticService;

/**
 * Statistic controller.
 *
 * @author ryanwang
 * @date 2019-12-16
 */
@RestController
@RequestMapping("/api/admin/statistics")
public class StatisticController {

    private final StatisticService statisticService;

    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @GetMapping
    public StatisticDTO statistics() {
        return statisticService.getStatistic();
    }

    @GetMapping("user")
    public StatisticWithUserDTO statisticsWithUser() {
        return statisticService.getStatisticWithUser();
    }
}
