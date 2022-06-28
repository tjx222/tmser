package com.tmser.blog.controller.admin.api;

import com.tmser.blog.model.dto.LinkDTO;
import com.tmser.blog.model.entity.Link;
import com.tmser.blog.model.params.LinkParam;
import com.tmser.blog.service.LinkService;
import com.tmser.model.sort.Sort;
import com.tmser.spring.web.SortDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.tmser.model.sort.Sort.Direction.ASC;

/**
 * Link Controller
 *
 * @author ryanwang
 * @date 2019-03-21
 */
@RestController
@RequestMapping("/api/admin/links")
public class LinkController {

    private final LinkService linkService;

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @GetMapping
    public List<LinkDTO> listLinks(@SortDefault(sort = "team,DESC") Sort sort) {
        return linkService.listDtos(sort.and(Sort.by(ASC, "priority")));
    }

    @GetMapping("{id:\\d+}")
    public LinkDTO getBy(@PathVariable("id") Integer id) {
        return new LinkDTO().convertFrom(linkService.getById(id));
    }

    @PostMapping
    public LinkDTO createBy(@RequestBody @Valid LinkParam linkParam) {
        Link link = linkService.createBy(linkParam);
        return new LinkDTO().convertFrom(link);
    }

    @PutMapping("{id:\\d+}")
    public LinkDTO updateBy(@PathVariable("id") Integer id,
                            @RequestBody @Valid LinkParam linkParam) {
        Link link = linkService.updateBy(id, linkParam);
        return new LinkDTO().convertFrom(link);
    }

    @DeleteMapping("{id:\\d+}")
    public void deletePermanently(@PathVariable("id") Integer id) {
        linkService.removeById(id);
    }

    @GetMapping("teams")
    public List<String> teams() {
        return linkService.listAllTeams();
    }
}
