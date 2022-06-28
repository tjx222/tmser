package com.tmser.blog.model.dto;

import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import com.tmser.blog.model.dto.base.OutputConverter;
import com.tmser.blog.model.entity.Category;

/**
 * Category output dto.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-03-19
 */
@Data
@ToString
@EqualsAndHashCode
public class CategoryDTO implements OutputConverter<CategoryDTO, Category> {

    private Integer id;

    private String name;

    private String slug;

    private String description;

    private String thumbnail;

    private Integer parentId;

    private String password;

    private Date createTime;

    private String fullPath;

    private Integer priority;
}
