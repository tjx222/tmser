package com.tmser.blog.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import com.tmser.blog.model.dto.BaseCommentDTO;

/**
 * Comment including replied count.
 *
 * @author johnniang
 * @date 19-5-14
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CommentWithHasChildrenVO extends BaseCommentDTO {

    private boolean hasChildren;
}
