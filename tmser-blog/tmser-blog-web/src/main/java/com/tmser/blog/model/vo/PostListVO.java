package com.tmser.blog.model.vo;

import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.tmser.blog.model.dto.CategoryDTO;
import com.tmser.blog.model.dto.TagDTO;
import com.tmser.blog.model.dto.post.BasePostSimpleDTO;

/**
 * Post list vo.
 *
 * @author johnniang
 * @author guqing
 * @author ryanwang
 * @date 2019-03-19
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostListVO extends BasePostSimpleDTO {

    private Long commentCount;

    private List<TagDTO> tags;

    private List<CategoryDTO> categories;

    private Map<String, Object> metas;
}
