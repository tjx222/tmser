package com.tmser.blog.model.dto;

import java.util.Date;
import lombok.Data;
import com.tmser.blog.model.dto.base.OutputConverter;
import com.tmser.blog.model.entity.Journal;
import com.tmser.blog.model.enums.JournalType;

/**
 * Journal dto.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-04-24
 */
@Data
public class JournalDTO implements OutputConverter<JournalDTO, Journal> {

    private Integer id;

    private String sourceContent;

    private String content;

    private Long likes;

    private Date createTime;

    private JournalType type;
}
