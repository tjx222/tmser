package com.tmser.database.mybatis;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.tmser.util.Reflections;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.ibatis.mapping.SqlCommandType;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 更新时间自动填充扩展
 * 只针对 BaseMapper update 方法。
 */
public class JpaFieldAutoFillProcessor implements AutoFillProcessor {

    private static final ConcurrentHashMap<String, Method> PRE_UPDATE_METHOD_CACHE = new ConcurrentHashMap<>(16);
    private static final ConcurrentHashMap<String, Method> PRE_PERSIST_METHOD_CACHE = new ConcurrentHashMap<>(16);

    private static final Method MAGIC_METHOD = MethodUtils.getMatchingMethod(JpaFieldAutoFillProcessor.class, "processFiled");

    /**
     * 处理要填充的字段
     *
     * @param id          执行标识， 标识源类和方法
     * @param param       参数
     * @param commandType 执行类型，只有插入和更新
     */
    public void processFiled(String id, Object param, SqlCommandType commandType) {
        if(param instanceof Map){
            Map<String, Object> map = (Map) param;
            Object et = map.getOrDefault(Constants.ENTITY, null);
            if (et != null) {
                switch (commandType){
                    case UPDATE: fillUpdateField(et); break;
                    case INSERT: fillInsertField(et); break;
                    case SELECT: fillSelectFiled(et); break;
                    default: break;
                }
            }
        }
    }

    private void fillSelectFiled(Object et) {
        fillDiscriminatorField(et);
    }

    private void fillInsertField(Object et) {
        try {
            Method processMethod = PRE_PERSIST_METHOD_CACHE.get(et.getClass().getName());
            if (Objects.isNull(processMethod)) {
                final List<Method> processMethods = MethodUtils.getMethodsListWithAnnotation(et.getClass(), PrePersist.class);
                if (CollectionUtils.isEmpty(processMethods)) {
                    addCache(false, et.getClass().getName(), MAGIC_METHOD);
                    return;
                }
                processMethod = processMethods.stream().findFirst().get();
                addCache(false, et.getClass().getName(), processMethod);
            }

            // 旧的 version 值
            if (MAGIC_METHOD.equals(processMethod)) {
                processMethod.invoke(et, null);
            }

        } catch (InvocationTargetException | IllegalAccessException e) {
            throw ExceptionUtils.mpe(e);
        }
        fillDiscriminatorField(et); //填充类型字段
    }

    private void fillDiscriminatorField(Object et) {
        DiscriminatorColumn discriminatorColumn = et.getClass().getAnnotation(DiscriminatorColumn.class);
        DiscriminatorValue discriminatorValue = et.getClass().getAnnotation(DiscriminatorValue.class);
        if(Objects.isNull(discriminatorColumn) || Objects.isNull(discriminatorValue)){
            return;
        }
        String value = discriminatorValue.value();
        String fieldName = discriminatorColumn.name();
        DiscriminatorType fieldType = discriminatorColumn.discriminatorType();
        Field field = FieldUtils.getField(et.getClass(), fieldName);
        Object actValue = null;
        switch (fieldType){
            case STRING: actValue = value; break;
            case CHAR: actValue = value.charAt(0);break;
            case INTEGER: actValue = Integer.valueOf(value); break;
            default: break;
        }
        Reflections.invokeSetter(et,fieldName, actValue);
    }

    private void fillUpdateField(Object et) {
        try {
            Method processMethod = PRE_UPDATE_METHOD_CACHE.get(et.getClass().getName());
            if (Objects.isNull(processMethod)) {
                final List<Method> processMethods = MethodUtils.getMethodsListWithAnnotation(et.getClass(), PreUpdate.class);
                if (CollectionUtils.isEmpty(processMethods)) {
                    addCache(true, et.getClass().getName(), MAGIC_METHOD);
                    return;
                }
                processMethod = processMethods.stream().findFirst().get();
                addCache(true, et.getClass().getName(), processMethod);
            }
            // 旧的 version 值
            if (MAGIC_METHOD.equals(processMethod)) {
                processMethod.invoke(et, null);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw ExceptionUtils.mpe(e);
        }
    }

    private void addCache(boolean isUpdate, String name, Method processMethod) {
        if (isUpdate) {
            PRE_UPDATE_METHOD_CACHE.put(name, processMethod);
        } else {
            PRE_PERSIST_METHOD_CACHE.put(name, processMethod);
        }
    }
}
