package com.tmser.blog.model.dto;

import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import com.tmser.blog.model.dto.base.OutputConverter;
import com.tmser.blog.model.entity.BaseComment;
import com.tmser.blog.model.enums.CommentStatus;

/**
 * Base comment output dto.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-03-20
 */
@Data
@ToString
@EqualsAndHashCode
public class BaseCommentDTO implements OutputConverter<BaseCommentDTO, BaseComment> {

    private Long id;

    private String author;

    private String email;

    private String ipAddress;

    private String authorUrl;

    private String gravatarMd5;

    private String content;

    private CommentStatus status;

    private String userAgent;

    private Long parentId;

    private Boolean isAdmin;

    private Boolean allowNotification;

    private Date createTime;

    private String avatar;
}
