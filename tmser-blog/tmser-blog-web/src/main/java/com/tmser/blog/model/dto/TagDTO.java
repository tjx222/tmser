package com.tmser.blog.model.dto;

import java.util.Date;
import lombok.Data;
import com.tmser.blog.model.dto.base.OutputConverter;
import com.tmser.blog.model.entity.Tag;
import com.tmser.blog.model.support.HaloConst;

/**
 * Tag output dto.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-03-19
 */
@Data
public class TagDTO implements OutputConverter<TagDTO, Tag> {

    private Integer id;

    private String name;

    private String slug;

    private String color = HaloConst.DEFAULT_TAG_COLOR;

    private String thumbnail;

    private Date createTime;

    private String fullPath;
}
