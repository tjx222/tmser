package com.tmser.blog.model.vo;

import java.util.LinkedList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import com.tmser.blog.model.dto.MenuDTO;

/**
 * @author ryanwang
 * @date 2019-04-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MenuVO extends MenuDTO {

    private List<MenuVO> children = new LinkedList<>();
}
