package com.tmser.blog.model.vo;

import java.util.List;
import lombok.Data;
import lombok.ToString;
import com.tmser.blog.model.dto.PhotoDTO;

/**
 * Link team vo.
 *
 * @author ryanwang
 * @date 2019/3/22
 */
@Data
@ToString
public class PhotoTeamVO {

    private String team;

    private List<PhotoDTO> photos;
}
