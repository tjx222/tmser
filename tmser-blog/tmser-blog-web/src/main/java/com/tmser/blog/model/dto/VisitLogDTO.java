package com.tmser.blog.model.dto;

import com.tmser.blog.model.dto.base.OutputConverter;
import com.tmser.blog.model.entity.VisitLog;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import java.util.Date;

/**
 * @author tmser
 * @date 7/25/22
 */
@Data
@ToString
@EqualsAndHashCode
public class VisitLogDTO implements OutputConverter<VisitLogDTO, VisitLog> {

    private Integer id;

    private Integer contentId;

    private Integer shareId;

    private String shareName;

    private String contentName;

    private String ipAddress;

    private Date createTime;
}
