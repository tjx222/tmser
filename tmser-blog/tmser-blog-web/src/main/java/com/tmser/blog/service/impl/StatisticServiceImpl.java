package com.tmser.blog.service.impl;

import com.tmser.blog.service.*;
import org.springframework.stereotype.Service;
import com.tmser.blog.exception.ServiceException;
import com.tmser.blog.model.dto.StatisticDTO;
import com.tmser.blog.model.dto.StatisticWithUserDTO;
import com.tmser.blog.model.dto.UserDTO;
import com.tmser.blog.model.entity.User;
import com.tmser.blog.model.enums.CommentStatus;
import com.tmser.blog.model.enums.PostStatus;

/**
 * Statistic service implementation.
 *
 * @author ryanwang
 * @date 2019-12-16
 */
@Service
public class StatisticServiceImpl implements StatisticService {

    private final AttachmentService attachmentService;

    private final ShareInfoService shareInfoService;

    private final OptionService optionService;

    private final UserService userService;

    public StatisticServiceImpl(AttachmentService attachmentService,
        ShareInfoService shareInfoService,
        OptionService optionService,
        UserService userService) {
        this.attachmentService = attachmentService;
        this.shareInfoService = shareInfoService;
        this.optionService = optionService;
        this.userService = userService;
    }

    @Override
    public StatisticDTO getStatistic() {
        StatisticDTO statisticDto = new StatisticDTO();
        statisticDto.setPostCount(attachmentService.count());

        // Handle comment count
        long birthday = optionService.getBirthday();
        long days = (System.currentTimeMillis() - birthday) / (1000 * 24 * 3600);
        statisticDto.setEstablishDays(days);
        statisticDto.setBirthday(birthday);

        statisticDto.setVisitCount(shareInfoService.sumTotalVisit());
        return statisticDto;
    }

    @Override
    public StatisticWithUserDTO getStatisticWithUser() {

        StatisticDTO statisticDto = getStatistic();

        StatisticWithUserDTO statisticWithUserDto = new StatisticWithUserDTO();
        statisticWithUserDto.convertFrom(statisticDto);

        User user =
            userService.getCurrentUser().orElseThrow(() -> new ServiceException("未查询到博主信息"));
        statisticWithUserDto.setUser(new UserDTO().convertFrom(user));

        return statisticWithUserDto;
    }
}
