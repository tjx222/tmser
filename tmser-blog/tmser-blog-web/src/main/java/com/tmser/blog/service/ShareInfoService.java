package com.tmser.blog.service;

import com.tmser.blog.model.dto.ShareInfoDTO;
import com.tmser.blog.model.dto.VisitLogDTO;
import com.tmser.blog.model.entity.ShareInfo;
import com.tmser.blog.model.entity.VisitLog;
import com.tmser.blog.model.params.ShareInfoParam;
import com.tmser.blog.service.base.CrudService;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import com.tmser.model.page.Pageable;

import java.util.List;

/**
 * Log service interface.
 *
 * @author johnniang
 * @date 2019-03-14
 */
public interface ShareInfoService extends CrudService<ShareInfo, Integer> {

    /**
     * Lists latest logs.
     *
     * @param top top number must not be less than 0
     * @return a page of latest logs
     */
    Page<ShareInfoDTO> pageLatest(int top);

    /**
     * 统计总访问次数
     * @return
     */
    Long sumTotalVisit();

    /**
     * 删除
     * 逻辑删除
     * @return
     */
    ShareInfoDTO deleteById(Integer id);

    /**
     * 批量删除
     * 逻辑删除
     * @return
     */
    List<ShareInfoDTO> deleteByIds(List<Integer> id);

    /**
     * 分页按条件查询
     * @param pageable
     * @param shareInfoParam
     * @return
     */
    Page<ShareInfoDTO> pageDtosBy(Pageable pageable, ShareInfoParam shareInfoParam);
}
