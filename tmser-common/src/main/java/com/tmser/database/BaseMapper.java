package com.tmser.database;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 基础Mapper
 *
 * @param <T>
 */
public interface BaseMapper<T> {

    /**
     * 根据 bizID 查询
     *
     * @param bizId 主键ID
     */
    T selectByBizId(@Param("bizId") String bizId);

    /**
     * 查询（根据bizID 批量查询）
     *
     * @param bizIdList 主键ID列表(不能为 null 以及 empty)
     */
    List<T> selectByBizIds(@Param(Constants.COLLECTION) Collection<String> bizIdList);

}
