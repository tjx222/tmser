package com.tmser.blog.model.entity;

import com.tmser.blog.model.enums.OptionType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

/**
 * Setting entity.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-03-20
 */
@Data
@Entity
@Table(name = "options")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Option extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "custom-id")
    private Integer id;

    /**
     * option type
     */
    @Column(name = "type")
    private OptionType type;

    /**
     * option key
     */
    @Column(name = "option_key", length = 100, nullable = false)
    private String optionKey;

    /**
     * option value
     */
    @Column(name = "option_value", nullable = false)
    @Lob
    private String optionValue;

    public Option(String optionKey, String optionValue) {
        this.optionKey = optionKey;
        this.optionValue = optionValue;
    }

    public Option(OptionType type, String optionKey, String optionValue) {
        this.type = type;
        this.optionKey = optionKey;
        this.optionValue = optionValue;
    }

    @Override
    public void prePersist() {
        super.prePersist();

        if (type == null) {
            type = OptionType.INTERNAL;
        }
    }
}
