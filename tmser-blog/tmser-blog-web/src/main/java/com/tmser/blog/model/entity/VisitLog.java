package com.tmser.blog.model.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.tmser.blog.model.enums.LogType;
import com.tmser.blog.utils.DateUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

/**
 * Log entity.
 *
 * @author johnniang
 */
@Data
@Entity
@Table(name = "visit_log", indexes = {@Index(name = "index_share_id", columnList = "share_id")})
@ToString
@EqualsAndHashCode
public class VisitLog{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 访问内容id
     */
    @Column(name = "content_id")
    private Integer contentId;

    /**
     * Log type.
     */
    @Column(name = "share_id", nullable = false)
    private Integer shareId;

    /**
     * Operator's ip address.
     */
    @Column(name = "ip_address", length = 127)
    private String ipAddress;

    @Column(name = "share_name")
    private String shareName;

    @Column(name = "content_name")
    private String contentName;

    /**
     * Create time.
     */
    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @PrePersist
    protected void prePersist() {
        Date now = DateUtils.now();
        if (createTime == null) {
            createTime = now;
        }
    }
}
