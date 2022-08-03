package com.tmser.blog.model.dto;

import com.tmser.blog.model.dto.base.OutputConverter;
import com.tmser.blog.model.entity.Log;
import com.tmser.blog.model.entity.ShareInfo;
import com.tmser.blog.model.enums.LogType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

/**
 * @author tmser
 * @date 7/25/22
 */
@Data
@ToString
@EqualsAndHashCode
public class ShareInfoDTO implements OutputConverter<ShareInfoDTO, ShareInfo> {

    private Integer id;

    private String name;

    private String remark;

    private Integer createId;

    private Integer validDays;

    private String sign;

    private List<Integer> resIds;

    private Integer totalVisit;

    private Date startTime;

    private Date endTime;

    private Date createTime;

    private Integer deleted;
}
