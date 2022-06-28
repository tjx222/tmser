package com.tmser.blog.model.dto.post;

import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import com.tmser.blog.model.dto.base.OutputConverter;
import com.tmser.blog.model.entity.BasePost;
import com.tmser.blog.model.enums.PostEditorType;
import com.tmser.blog.model.enums.PostStatus;

/**
 * Base post minimal output dto.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-03-19
 */
@Data
@ToString
@EqualsAndHashCode
public class BasePostMinimalDTO implements OutputConverter<BasePostMinimalDTO, BasePost> {

    private Integer id;

    private String title;

    private PostStatus status;

    private String slug;

    private PostEditorType editorType;

    private Date updateTime;

    private Date createTime;

    private Date editTime;

    private String metaKeywords;

    private String metaDescription;

    private String fullPath;
}
