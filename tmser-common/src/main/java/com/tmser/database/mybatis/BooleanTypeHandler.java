package com.tmser.database.mybatis;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * boolean 类型转换， 0 或 null 为false, 其他为true
 */
public class BooleanTypeHandler implements TypeHandler<Boolean> {

    @Override
    public void setParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            parameter = false;
        }
        int value = parameter ? 1 : 0;
        ps.setInt(i, value);
    }

    @Override
    public Boolean getResult(ResultSet rs, String columnName) throws SQLException {
        if (columnName == null) {
            columnName = "0";
        }
        Boolean rt = Boolean.TRUE;
        if (rs.getInt(columnName) == 0) {
            rt = Boolean.FALSE;
        }
        return rt;
    }

    @Override
    public Boolean getResult(ResultSet rs, int columnIndex) throws SQLException {
        int b = rs.getInt(columnIndex);
        return b != 0;
    }

    @Override
    public Boolean getResult(CallableStatement cs, int columnIndex) throws SQLException {
        if (cs == null) {
            return false;
        }
        int b = cs.getInt(columnIndex);
        return b != 0;
    }

}
