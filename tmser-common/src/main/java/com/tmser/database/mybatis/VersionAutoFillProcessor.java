package com.tmser.database.mybatis;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import org.apache.ibatis.mapping.SqlCommandType;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 乐观锁，插入时填充版本号
 */
public class VersionAutoFillProcessor implements AutoFillProcessor{

    /**
     * 处理要填充的字段
     * @param id 执行标识， 标识源类和方法
     * @param param 参数
     * @param commandType  执行类型，只有插入和更新
     */
    public void processFiled(String id, Object param, SqlCommandType commandType){
        if(SqlCommandType.INSERT == commandType){
            Object et = param;
            if (et != null) {
                // entity
                TableInfo tableInfo = TableInfoHelper.getTableInfo(et.getClass());
                if (tableInfo == null || !tableInfo.isWithVersion()) {
                    return;
                }
                try {
                    TableFieldInfo fieldInfo = tableInfo.getVersionFieldInfo();
                    Field versionField = fieldInfo.getField();
                    // 旧的 version 值
                    Object originalVersionVal = versionField.get(et);
                    if (originalVersionVal == null) {
                        versionField.set(et, getDefaultVersionVal(fieldInfo.getPropertyType()));
                    }
                } catch (IllegalAccessException e) {
                    throw ExceptionUtils.mpe(e);
                }
            }
        }
    }

    protected Object getDefaultVersionVal(Class<?> clazz) {
        if (long.class.equals(clazz) || Long.class.equals(clazz)) {
            return (long)  0;
        } else if (int.class.equals(clazz) || Integer.class.equals(clazz)) {
            return (int) 0;
        } else if (Date.class.equals(clazz)) {
            return new Date();
        } else if (Timestamp.class.equals(clazz)) {
            return new Timestamp(System.currentTimeMillis());
        } else if (LocalDateTime.class.equals(clazz)) {
            return LocalDateTime.now();
        }
        return null;
    }
}
