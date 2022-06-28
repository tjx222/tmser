package com.tmser.blog.model.dto.post;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.lang.NonNull;
import com.tmser.blog.model.entity.BasePost;
import com.tmser.blog.model.entity.Content.PatchedContent;

/**
 * Base post detail output dto.
 *
 * @author johnniang
 * @author guqing
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class BasePostDetailDTO extends BasePostSimpleDTO {

    private String originalContent;

    private String content;

    private Long commentCount;

    @Override
    @NonNull
    @SuppressWarnings("unchecked")
    public <T extends BasePostMinimalDTO> T convertFrom(@NonNull BasePost domain) {
        BasePostDetailDTO postDetailDTO = super.convertFrom(domain);
        PatchedContent content = domain.getContent();
        postDetailDTO.setContent(content.getContent());
        postDetailDTO.setOriginalContent(content.getOriginalContent());
        return (T) postDetailDTO;
    }

    /**
     * Compatible with the formatContent attribute existing in the old version
     * it will be removed in v2.0
     *
     * @return formatted post content
     */
    @Deprecated()
    public String getFormatContent() {
        return this.content;
    }
}
