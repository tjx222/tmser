package com.tmser.blog.controller.content.api;

import com.tmser.blog.model.dto.LinkDTO;
import com.tmser.blog.model.vo.LinkTeamVO;
import com.tmser.blog.service.LinkService;
import com.tmser.model.sort.Sort;
import com.tmser.spring.web.SortDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Content link controller.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-04-03
 */
@RestController("ApiContentLinkController")
@RequestMapping("/api/content/links")
public class LinkController {

    private final LinkService linkService;

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @GetMapping
    public List<LinkDTO> listLinks(@SortDefault(sort = "create_time, DESC") Sort sort) {
        return linkService.listDtos(sort);
    }

    @GetMapping("team_view")
    public List<LinkTeamVO> listTeamVos(Sort sort) {
        return linkService.listTeamVos(sort);
    }
}
