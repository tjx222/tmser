package com.tmser.database.mybatis;

import org.apache.ibatis.mapping.SqlCommandType;

/**
 * 自动填充扩展
 */
public interface AutoFillProcessor {

    /**
     * 处理要填充的字段
     * @param id 执行标识， 标识源类和方法
     * @param param 参数
     * @param commandType  执行类型，只有插入和更新
     */
    void processFiled(String id, Object param, SqlCommandType commandType);
}
