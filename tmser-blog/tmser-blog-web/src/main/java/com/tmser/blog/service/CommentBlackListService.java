package com.tmser.blog.service;

import com.tmser.blog.model.entity.CommentBlackList;
import com.tmser.blog.model.enums.CommentViolationTypeEnum;
import com.tmser.blog.service.base.CrudService;

/**
 * Comment BlackList Service
 *
 * @author Lei XinXin
 * @date 2020/1/3
 */
public interface CommentBlackListService extends CrudService<CommentBlackList, Long> {
    /**
     * 评论封禁状态
     *
     * @param ipAddress ip地址
     * @return boolean
     */
    CommentViolationTypeEnum commentsBanStatus(String ipAddress);
}
