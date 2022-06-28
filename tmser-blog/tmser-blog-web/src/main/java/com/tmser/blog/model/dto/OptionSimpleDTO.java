package com.tmser.blog.model.dto;

import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.tmser.blog.model.enums.OptionType;

/**
 * Option list output dto.
 *
 * @author ryanwang
 * @date 2019-12-02
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OptionSimpleDTO extends OptionDTO {

    private Integer id;

    private OptionType type;

    private Date createTime;

    private Date updateTime;
}
