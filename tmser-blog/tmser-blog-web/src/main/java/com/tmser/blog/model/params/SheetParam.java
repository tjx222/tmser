package com.tmser.blog.model.params;

import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import com.tmser.blog.model.dto.base.InputConverter;
import com.tmser.blog.model.entity.Sheet;
import com.tmser.blog.model.entity.SheetMeta;
import com.tmser.blog.model.enums.PostEditorType;
import com.tmser.blog.utils.SlugUtils;

/**
 * Sheet param.
 *
 * @author johnniang
 * @author ryanwang
 * @author guqing
 * @date 2019-4-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SheetParam extends BasePostParam implements InputConverter<Sheet> {

    private Set<SheetMetaParam> metas;

    @Override
    @Size(max = 255, message = "页面别名的字符长度不能超过 {max}")
    public String getSlug() {
        return super.getSlug();
    }

    @Override
    @NotBlank(message = "页面标题不能为空")
    @Size(max = 100, message = "页面标题的字符长度不能超过 {max}")
    public String getTitle() {
        return super.getTitle();
    }

    @Override
    @Size(max = 255, message = "页面密码的字符长度不能超过 {max}")
    public String getPassword() {
        return super.getPassword();
    }

    public Set<SheetMeta> getSheetMetas() {
        Set<SheetMeta> sheetMetasSet = new HashSet<>();
        if (CollectionUtils.isEmpty(metas)) {
            return sheetMetasSet;
        }

        for (SheetMetaParam sheetMetaParam : metas) {
            SheetMeta sheetMeta = sheetMetaParam.convertTo();
            sheetMetasSet.add(sheetMeta);
        }
        return sheetMetasSet;
    }

    @Override
    public Sheet convertTo() {
        slug = StringUtils.isBlank(slug) ? SlugUtils.slug(title) : SlugUtils.slug(slug);

        if (null == thumbnail) {
            thumbnail = "";
        }

        if (null == editorType) {
            editorType = PostEditorType.MARKDOWN;
        }

        Sheet sheet = InputConverter.super.convertTo();
        populateContent(sheet);
        return sheet;
    }

    @Override
    public void update(Sheet sheet) {
        slug = StringUtils.isBlank(slug) ? SlugUtils.slug(title) : SlugUtils.slug(slug);

        if (null == thumbnail) {
            thumbnail = "";
        }

        if (null == editorType) {
            editorType = PostEditorType.MARKDOWN;
        }
        populateContent(sheet);
        InputConverter.super.update(sheet);
    }
}
