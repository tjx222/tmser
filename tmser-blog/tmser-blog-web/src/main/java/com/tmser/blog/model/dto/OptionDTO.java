package com.tmser.blog.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.tmser.blog.model.dto.base.OutputConverter;
import com.tmser.blog.model.entity.Option;

/**
 * Option output dto.
 *
 * @author johnniang
 * @date 3/20/19
 */
@NoArgsConstructor
@AllArgsConstructor
public class OptionDTO implements OutputConverter<OptionDTO, Option> {
    private String optionKey;

    private String optionValue;

    public String getKey() {
        return optionKey;
    }

    public String getValue() {
        return optionValue;
    }

    public String getOptionKey() {
        return optionKey;
    }

    public void setOptionKey(String optionKey) {
        this.optionKey = optionKey;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }
}
