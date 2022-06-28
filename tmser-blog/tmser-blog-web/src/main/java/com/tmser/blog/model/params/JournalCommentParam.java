package com.tmser.blog.model.params;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import com.tmser.blog.model.entity.JournalComment;

/**
 * Journal comment param.
 *
 * @author johnniang
 * @date 3/22/19
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class JournalCommentParam extends BaseCommentParam<JournalComment> {

}
