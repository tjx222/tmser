package com.tmser.blog.controller.admin.api;

import com.tmser.blog.model.dto.MenuDTO;
import com.tmser.blog.model.dto.base.InputConverter;
import com.tmser.blog.model.entity.Menu;
import com.tmser.blog.model.params.MenuParam;
import com.tmser.blog.model.vo.MenuVO;
import com.tmser.blog.service.MenuService;
import com.tmser.model.sort.Sort;
import com.tmser.spring.web.SortDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.tmser.model.sort.Sort.Direction.ASC;

/**
 * Menu controller.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-04-03
 */
@RestController
@RequestMapping("/api/admin/menus")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public List<MenuDTO> listAll(@SortDefault(sort = "team, DESC") Sort sort) {
        return menuService.listDtos(sort.and(Sort.by(ASC, "priority")));
    }

    @GetMapping("tree_view")
    public List<MenuVO> listAsTree(@SortDefault(sort = "team, DESC") Sort sort) {
        return menuService.listAsTree(sort.and(Sort.by(ASC, "priority")));
    }

    @GetMapping("team/tree_view")
    public List<MenuVO> listDefaultsAsTreeByTeam(
            @SortDefault(sort = "priority, ASC") Sort sort,
            @RequestParam(name = "team") String team) {
        return menuService.listByTeamAsTree(team, sort);
    }

    @GetMapping("{menuId:\\d+}")
    public MenuDTO getBy(@PathVariable("menuId") Integer menuId) {
        return new MenuDTO().convertFrom(menuService.getById(menuId));
    }

    @PostMapping
    public MenuDTO createBy(@RequestBody @Valid MenuParam menuParam) {
        return new MenuDTO().convertFrom(menuService.createBy(menuParam));
    }

    @PostMapping("/batch")
    public List<MenuDTO> createBatchBy(@RequestBody @Valid List<MenuParam> menuParams) {
        List<Menu> menus = menuParams
                .stream()
                .map(InputConverter::convertTo)
                .collect(Collectors.toList());
        return menuService.createInBatch(menus).stream()
                .map(menu -> (MenuDTO) new MenuDTO().convertFrom(menu))
                .collect(Collectors.toList());
    }

    @PutMapping("{menuId:\\d+}")
    public MenuDTO updateBy(@PathVariable("menuId") Integer menuId,
                            @RequestBody @Valid MenuParam menuParam) {
        // Get the menu
        Menu menu = menuService.getById(menuId);

        // Update changed properties of the menu
        menuParam.update(menu);

        // Update menu in database
        return new MenuDTO().convertFrom(menuService.update(menu));
    }

    @PutMapping("/batch")
    public List<MenuDTO> updateBatchBy(@RequestBody @Valid List<MenuParam> menuParams) {
        List<Menu> menus = menuParams
                .stream()
                .filter(menuParam -> Objects.nonNull(menuParam.getId()))
                .map(InputConverter::convertTo)
                .collect(Collectors.toList());
        return menuService.updateInBatch(menus).stream()
                .map(menu -> (MenuDTO) new MenuDTO().convertFrom(menu))
                .collect(Collectors.toList());
    }

    @DeleteMapping("{menuId:\\d+}")
    public MenuDTO deleteBy(@PathVariable("menuId") Integer menuId) {
        List<Menu> menus = menuService.listByParentId(menuId);
        if (null != menus && menus.size() > 0) {
            menus.forEach(menu -> {
                menu.setParentId(0);
                menuService.update(menu);
            });
        }
        return new MenuDTO().convertFrom(menuService.removeById(menuId));
    }

    @DeleteMapping("/batch")
    public List<MenuDTO> deleteBatchBy(@RequestBody List<Integer> menuIds) {
        List<Menu> menus = menuService.listAllByIds(menuIds);
        menuService.removeInBatch(menuIds);
        return menus.stream()
                .map(menu -> (MenuDTO) new MenuDTO().convertFrom(menu))
                .collect(Collectors.toList());
    }

    @GetMapping("teams")
    public List<String> teams() {
        return menuService.listAllTeams();
    }
}
