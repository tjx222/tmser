package com.tmser.blog.service.impl;

import com.tmser.blog.model.dto.MenuDTO;
import com.tmser.blog.model.entity.Menu;
import com.tmser.blog.model.params.MenuParam;
import com.tmser.blog.model.vo.MenuTeamVO;
import com.tmser.blog.model.vo.MenuVO;
import com.tmser.blog.repository.MenuRepository;
import com.tmser.blog.service.MenuService;
import com.tmser.blog.service.base.AbstractCrudService;
import com.tmser.blog.utils.BeanUtils;
import com.tmser.blog.utils.ServiceUtils;
import com.tmser.model.sort.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * MenuService implementation class.
 *
 * @author ryanwang
 * @date 2019-03-14
 */
@Service
public class MenuServiceImpl extends AbstractCrudService<Menu, Integer> implements MenuService {

    private final MenuRepository menuRepository;

    public MenuServiceImpl(MenuRepository menuRepository) {
        super(menuRepository);
        this.menuRepository = menuRepository;
    }

    @Override
    public @NonNull
    List<MenuDTO> listDtos(@NonNull Sort sort) {
        Assert.notNull(sort, "Sort info must not be null");

        return convertTo(listAll(sort));
    }

    @Override
    public @NonNull
    List<MenuTeamVO> listTeamVos(@NonNull Sort sort) {
        Assert.notNull(sort, "Sort info must not be null");

        // List all menus
        List<MenuDTO> menus = listDtos(sort);

        // Get teams
        Set<String> teams = ServiceUtils.fetchProperty(menus, MenuDTO::getTeam);

        // Convert to team menu list map (Key: team, value: menu list)
        Map<String, List<MenuDTO>> teamMenuListMap =
                ServiceUtils.convertToListMap(teams, menus, MenuDTO::getTeam);

        List<MenuTeamVO> result = new LinkedList<>();

        // Wrap menu team vo list
        teamMenuListMap.forEach((team, menuList) -> {
            // Build menu team vo
            MenuTeamVO menuTeamVO = new MenuTeamVO();
            menuTeamVO.setTeam(team);
            menuTeamVO.setMenus(menuList);

            // Add it to result
            result.add(menuTeamVO);
        });

        return result;
    }

    @Override
    public List<MenuDTO> listByTeam(@NonNull String team, Sort sort) {
        List<Menu> menus = menuRepository.findByTeam(team, sort);
        return menus.stream().map(menu -> (MenuDTO) new MenuDTO().convertFrom(menu))
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuVO> listByTeamAsTree(@NonNull String team, Sort sort) {
        Assert.notNull(team, "Team must not be null");

        List<Menu> menus = menuRepository.findByTeam(team, sort);

        if (CollectionUtils.isEmpty(menus)) {
            return Collections.emptyList();
        }

        MenuVO topLevelMenu = createTopLevelMenu();

        concreteTree(topLevelMenu, menus);

        return topLevelMenu.getChildren();
    }

    @Override
    public @NonNull
    Menu createBy(@NonNull MenuParam menuParam) {
        Assert.notNull(menuParam, "Menu param must not be null");

        // Create an return
        return create(menuParam.convertTo());
    }

    @Override
    public List<MenuVO> listAsTree(@NonNull Sort sort) {
        Assert.notNull(sort, "Sort info must not be null");

        // List all menu
        List<Menu> menus = listAll(sort);

        if (CollectionUtils.isEmpty(menus)) {
            return Collections.emptyList();
        }

        // Create top menu
        MenuVO topLevelMenu = createTopLevelMenu();

        // Concrete the tree
        concreteTree(topLevelMenu, menus);

        return topLevelMenu.getChildren();
    }

    @Override
    public List<Menu> listByParentId(@NonNull Integer id) {
        Assert.notNull(id, "Menu parent id must not be null");

        return menuRepository.findByParentId(id);
    }

    @Override
    public List<String> listAllTeams() {
        return menuRepository.findAllTeams();
    }

    @Override
    public @NonNull
    Menu create(@NonNull Menu menu) {
        return super.create(menu);
    }

    @Override
    public @NonNull
    Menu update(@NonNull Menu menu) {
        return super.update(menu);
    }

    @Override
    @NonNull
    @Transactional(rollbackFor = Exception.class)
    public List<Menu> updateInBatch(@NonNull Collection<Menu> menus) {
        Set<Integer> menuIds = ServiceUtils.fetchProperty(menus, Menu::getId);
        Map<Integer, Menu> idMenuParamMap = ServiceUtils.convertToMap(menus, Menu::getId);
        return menuIds.isEmpty() ? Collections.emptyList() : menuRepository.selectBatchIds(menuIds)
                .stream()
                .map(menuToUpdate -> {
                    Menu menuParam = idMenuParamMap.get(menuToUpdate.getId());
                    BeanUtils.updateProperties(menuParam, menuToUpdate);
                    return update(menuToUpdate);
                })
                .collect(Collectors.toList());
    }

    /**
     * Concrete menu tree.
     *
     * @param parentMenu parent menu vo must not be null
     * @param menus      a list of menu
     */
    private void concreteTree(MenuVO parentMenu, List<Menu> menus) {
        Assert.notNull(parentMenu, "Parent menu must not be null");

        if (CollectionUtils.isEmpty(menus)) {
            return;
        }

        // Create children container for removing after
        List<Menu> children = new LinkedList<>();

        menus.forEach(menu -> {
            if (parentMenu.getId().equals(menu.getParentId())) {
                // Save child menu
                children.add(menu);

                // Convert to child menu vo
                MenuVO child = new MenuVO().convertFrom(menu);

                // Init children if absent
                if (parentMenu.getChildren() == null) {
                    parentMenu.setChildren(new LinkedList<>());
                }
                parentMenu.getChildren().add(child);
            }
        });

        // Remove all child menus
        menus.removeAll(children);

        // Foreach children vos
        if (!CollectionUtils.isEmpty(parentMenu.getChildren())) {
            parentMenu.getChildren().forEach(childMenu -> concreteTree(childMenu, menus));
        }
    }

    /**
     * Creates a top level menu.
     *
     * @return top level menu with id 0
     */
    @NonNull
    private MenuVO createTopLevelMenu() {
        MenuVO topMenu = new MenuVO();
        // Set default value
        topMenu.setId(0);
        topMenu.setChildren(new LinkedList<>());
        topMenu.setParentId(-1);
        return topMenu;
    }

    private List<MenuDTO> convertTo(List<Menu> menus) {
        if (CollectionUtils.isEmpty(menus)) {
            return Collections.emptyList();
        }

        return menus.stream()
                .map(menu -> (MenuDTO) new MenuDTO().convertFrom(menu))
                .collect(Collectors.toList());
    }
}
