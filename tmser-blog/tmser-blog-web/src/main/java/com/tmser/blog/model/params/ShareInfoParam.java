package com.tmser.blog.model.params;

import com.tmser.blog.model.dto.base.InputConverter;
import com.tmser.blog.model.entity.Attachment;
import com.tmser.blog.model.entity.ShareInfo;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * Attachment params.
 *
 * @author ryanwang
 * @date 2019/04/20
 */
@Data
@ToString
public class ShareInfoParam implements InputConverter<ShareInfo> {

    private Integer id;

    private String name;

    private String remark;

    /**
     * 创建者
     */
    private Integer createId;

    /**
     * 有效天数
     */
    private Integer validDays;

    private String sign;

    private List<Integer> resIds;

    /**
     * 总访问次数
     */
    private Integer totalVisit;

    /**
     * Log type.
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

}
