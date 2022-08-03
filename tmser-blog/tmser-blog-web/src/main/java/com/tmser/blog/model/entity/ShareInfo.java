package com.tmser.blog.model.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Log entity.
 *
 * @author johnniang
 */
@Data
@Entity
@Table(name = "share_info", indexes = {@Index(name = "index_sign", columnList = "sign")})
@ToString
@EqualsAndHashCode(callSuper = true)
public class ShareInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * 创建者
     */
    @Column(name = "create_id")
    private Integer createId;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

    /**
     * 有效天数
     */
    @Column(name = "valid_days")
    private Integer validDays;

    @Column(name = "name",length = 32)
    private String name;

    @Column(name = "remark",length = 32)
    private String remark;

    @Column(name = "sign",length = 32)
    private String sign;

    @Column(name = "res_ids",length = 32)
    private List<Integer> resIds;

    /**
     * 总访问次数
     */
    @Column(name = "total_visit")
    private Integer totalVisit;

   @Column(name = "deleted")
    private Integer deleted;

    @Override
    public void preUpdate() {
        super.preUpdate();
        this.deleted = 0;
    }

}
