package com.tmser.blog.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import com.tmser.blog.model.enums.JournalType;

/**
 * Journal entity
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-03-22
 */
@Data
@Entity
@Table(name = "journals")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Journal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "custom-id")
    private Integer id;

    @Column(name = "source_content", nullable = false)
    @Lob
    private String sourceContent;

    @Column(name = "content", nullable = false)
    @Lob
    private String content;

    @Column(name = "likes")
    private Long likes;

    @Column(name = "type")
    private JournalType type;

    @Override
    public void prePersist() {
        super.prePersist();

        if (likes == null || likes < 0) {
            likes = 0L;
        }

        if (type == null) {
            type = JournalType.PUBLIC;
        }
    }
}
