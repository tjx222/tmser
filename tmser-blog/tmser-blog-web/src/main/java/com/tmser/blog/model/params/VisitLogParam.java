package com.tmser.blog.model.params;

import com.tmser.blog.model.dto.base.InputConverter;
import com.tmser.blog.model.entity.Log;
import com.tmser.blog.model.entity.VisitLog;
import com.tmser.blog.model.enums.LogType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author johnniang
 * @date 19-4-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VisitLogParam implements InputConverter<VisitLog> {

    private Integer shareId;

    private Integer contentId;

    private String shareName;

    private String contentName;

    private String ipAddress;

    public VisitLogParam(Integer shareId,
                         Integer contentId,
                         String shareName,
                         String contentName) {
        this.shareId = shareId;
        this.contentId = contentId;
        this.shareName = shareName;
        this.contentName = contentName;
    }
}
