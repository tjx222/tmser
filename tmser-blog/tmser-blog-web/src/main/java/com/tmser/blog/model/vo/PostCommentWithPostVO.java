package com.tmser.blog.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import com.tmser.blog.model.dto.BaseCommentDTO;
import com.tmser.blog.model.dto.post.BasePostMinimalDTO;

/**
 * PostComment list with post vo.
 *
 * @author johnniang
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class PostCommentWithPostVO extends BaseCommentDTO {

    private BasePostMinimalDTO post;
}
