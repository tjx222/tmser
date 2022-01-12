package com.tmser.database.mybatis;

import com.tmser.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * mybaits 数组类型转换
 *
 * <typeHandler handler="com.tmser.database.mybatis.ArrayTypeHandler" javaType="[Ljava.lang.Integer;"/>
 *
 * @param <T>
 */
@Slf4j
public class ArrayTypeHandler<T extends Object> extends BaseTypeHandler<T> {

    private Class<T> type;

    public ArrayTypeHandler(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int columnIndex, T parameter, JdbcType jdbcType) throws SQLException {
        try {
            String json = JsonUtil.toJson(parameter);
            ps.setString(columnIndex, json.substring(1, json.length() - 1));
        } catch (Exception e) {
            log.error("setNonNullParameter error:", e);
        }
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        try {
            String json = "[" + rs.getString(columnName) + "]";
            return JsonUtil.fromJson(json, type);
        } catch (Exception e) {
            log.error("getNullableResult error:", e);
        }
        return null;
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        try {
            String json = "[" + rs.getString(columnIndex) + "]";
            return JsonUtil.fromJson(json, type);
        } catch (Exception e) {
            log.error("getNullableResult error:", e);
        }
        return null;
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        try {
            String json = "[" + cs.getString(columnIndex) + "]";
            return JsonUtil.fromJson(json, type);
        } catch (Exception e) {
            log.error("getNullableResult error:", e);
        }
        return null;
    }
}
