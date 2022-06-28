package com.tmser.blog.model.params;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import com.tmser.blog.model.dto.base.InputConverter;
import com.tmser.blog.model.entity.Tag;
import com.tmser.blog.model.support.HaloConst;
import com.tmser.blog.utils.SlugUtils;

/**
 * Tag param.
 *
 * @author johnniang
 * @author ryanwang
 * @author guqing
 * @date 2019-03-20
 */
@Data
public class TagParam implements InputConverter<Tag> {

    @NotBlank(message = "标签名称不能为空")
    @Size(max = 255, message = "标签名称的字符长度不能超过 {max}")
    private String name;

    @Size(max = 255, message = "标签别名的字符长度不能超过 {max}")
    private String slug;

    @Size(max = 24, message = "颜色值字符长度不能超过 {max}")
    private String color;

    @Size(max = 1023, message = "封面图链接的字符长度不能超过 {max}")
    private String thumbnail;

    @Override
    public Tag convertTo() {

        slug = StringUtils.isBlank(slug) ? SlugUtils.slug(name) : SlugUtils.slug(slug);

        if (null == thumbnail) {
            thumbnail = "";
        }

        if (StringUtils.isBlank(color)) {
            this.color = HaloConst.DEFAULT_TAG_COLOR;
        }

        return InputConverter.super.convertTo();
    }

    @Override
    public void update(Tag tag) {

        slug = StringUtils.isBlank(slug) ? SlugUtils.slug(name) : SlugUtils.slug(slug);

        if (null == thumbnail) {
            thumbnail = "";
        }

        if (StringUtils.isBlank(color)) {
            this.color = HaloConst.DEFAULT_TAG_COLOR;
        }

        InputConverter.super.update(tag);
    }
}
