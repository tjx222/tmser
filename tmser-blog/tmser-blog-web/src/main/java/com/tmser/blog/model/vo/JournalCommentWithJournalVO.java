package com.tmser.blog.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import com.tmser.blog.model.dto.BaseCommentDTO;
import com.tmser.blog.model.dto.JournalDTO;

/**
 * Journal comment with journal vo.
 *
 * @author johnniang
 * @date 19-4-25
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class JournalCommentWithJournalVO extends BaseCommentDTO {

    private JournalDTO journal;
}
