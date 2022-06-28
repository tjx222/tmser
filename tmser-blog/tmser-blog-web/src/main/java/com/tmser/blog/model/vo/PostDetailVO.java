package com.tmser.blog.model.vo;

import java.util.List;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import com.tmser.blog.model.dto.BaseMetaDTO;
import com.tmser.blog.model.dto.CategoryDTO;
import com.tmser.blog.model.dto.TagDTO;
import com.tmser.blog.model.dto.post.BasePostDetailDTO;

/**
 * Post vo.
 *
 * @author johnniang
 * @author guqing
 * @date 2019-03-21
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PostDetailVO extends BasePostDetailDTO {

    private Set<Integer> tagIds;

    private List<TagDTO> tags;

    private Set<Integer> categoryIds;

    private List<CategoryDTO> categories;

    private Set<Long> metaIds;

    private List<BaseMetaDTO> metas;
}

