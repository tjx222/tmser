package com.tmser.blog.model.vo;

import java.util.List;
import lombok.Data;
import lombok.ToString;
import com.tmser.blog.model.dto.MenuDTO;

/**
 * Menu team vo.
 *
 * @author ryanwang
 * @date 2019/8/28
 */
@Data
@ToString
public class MenuTeamVO {

    private String team;

    private List<MenuDTO> menus;
}
