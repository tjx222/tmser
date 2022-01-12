package com.tmser.database.mybatis;

import com.tmser.model.money.Money;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>Title:实现Money类和Mysql DECIMAL字段的映射</p>
 * <p>Description:</p>
 */
public class MoneyTypeHandler extends BaseTypeHandler<Money> {

    /**
     * {@inheritDoc}
     *
     * @see BaseTypeHandler#setNonNullParameter(PreparedStatement, int, Object, JdbcType)
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
                                    Money parameter, JdbcType jdbcType) throws SQLException {
        ps.setBigDecimal(i, parameter.getAmount());
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseTypeHandler#getNullableResult(ResultSet, String)
     */
    @Override
    public Money getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        return createMoney(rs.getBigDecimal(columnName));
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseTypeHandler#getNullableResult(ResultSet, int)
     */
    @Override
    public Money getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        return createMoney(rs.getBigDecimal(columnIndex));
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseTypeHandler#getNullableResult(CallableStatement, int)
     */
    @Override
    public Money getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        return createMoney(cs.getBigDecimal(columnIndex));
    }

    private Money createMoney(BigDecimal value) {
        return value == null ? null : new Money(value);
    }
}
