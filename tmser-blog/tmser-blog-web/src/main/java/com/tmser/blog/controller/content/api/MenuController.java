package com.tmser.blog.controller.content.api;

import com.tmser.blog.model.dto.MenuDTO;
import com.tmser.blog.model.vo.MenuVO;
import com.tmser.blog.service.MenuService;
import com.tmser.model.sort.Sort;
import com.tmser.spring.web.SortDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Content menu controller.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-04-03
 */
@RestController("ApiContentMenuController")
@RequestMapping("/api/content/menus")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public List<MenuDTO> listAll(@SortDefault(sort = "priority, DESC") Sort sort) {
        return menuService.listDtos(sort);
    }

    @GetMapping(value = "tree_view")
    public List<MenuVO> listMenusTree(
            @SortDefault(sort = "createTime, DESC") Sort sort) {
        return menuService.listAsTree(sort);
    }
}
