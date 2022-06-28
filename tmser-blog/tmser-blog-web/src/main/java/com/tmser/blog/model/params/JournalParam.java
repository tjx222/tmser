package com.tmser.blog.model.params;

import javax.validation.constraints.NotBlank;
import lombok.Data;
import com.tmser.blog.model.dto.base.InputConverter;
import com.tmser.blog.model.entity.Journal;
import com.tmser.blog.model.enums.JournalType;

/**
 * Journal param.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-4-25
 */
@Data
public class JournalParam implements InputConverter<Journal> {

    @NotBlank(message = "内容不能为空")
    private String sourceContent;

    private JournalType type = JournalType.PUBLIC;
}
