package com.tmser.blog.model.params;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;
import com.tmser.blog.model.dto.base.InputConverter;
import com.tmser.blog.model.entity.Attachment;

/**
 * Attachment params.
 *
 * @author ryanwang
 * @date 2019/04/20
 */
@Data
public class AttachmentParam implements InputConverter<Attachment> {

    @NotBlank(message = "附件名称不能为空")
    @Size(max = 255, message = "附件名称的字符长度不能超过 {max}")
    private String name;

}
