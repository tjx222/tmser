package com.tmser.database.mybatis;

import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.tmser.database.IOptimisticLock;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Map;
import java.util.Optional;

public class OptimisticLockInterceptor implements InnerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(OptimisticLockInterceptor.class);
    private final static int SELECT_MAX_COUNT = 50000;
    private final static String SET_KEY = " set ";
    private final static String WHERE_KEY = " where ";

    private static String parseVersionSql(String sql, Integer version) {
        if (version == null) {
            return sql;
        }
        StringBuilder stringBuilder = new StringBuilder();
        sql = sql.toLowerCase();
        String updateClause = sql.split(SET_KEY)[0];
        String setAfter = sql.split(SET_KEY)[1];
        String setClause = setAfter.split(WHERE_KEY)[0];
        stringBuilder.append(updateClause).append(SET_KEY).append(setClause).append(",").append(" version =").append(version + 1)
                .append(WHERE_KEY).append("version=").append(version);
        if (setAfter.contains(WHERE_KEY)) {
            String whereClause = setAfter.split(WHERE_KEY)[1];
            stringBuilder.append(" and ").append(whereClause);
        }
        return stringBuilder.toString();
    }


    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        StatementHandler statementHandler = sh;
        MetaObject metaObject = MetaObject.forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        BoundSql boundSql = statementHandler.getBoundSql();
        String sql = boundSql.getSql();
        if (mappedStatement.getSqlCommandType() == SqlCommandType.UPDATE) {
            Optional<IOptimisticLock> iOptimisticLock = findIOptimisticLock(boundSql.getParameterObject());
            if (iOptimisticLock.isPresent()) {
                IOptimisticLock optimisticLock = iOptimisticLock.get();
                sql = parseVersionSql(sql, optimisticLock.getVersion());
            }
        }
        metaObject.setValue("delegate.boundSql.sql", sql);
    }

    /**
     * 查找分页参数
     * @param parameterObject 参数对象
     * @return 分页参数
     */
    public static Optional<IOptimisticLock> findIOptimisticLock(Object parameterObject) {
        if (parameterObject != null) {
            if (parameterObject instanceof Map) {
                Map<?, ?> parameterMap = (Map<?, ?>) parameterObject;
                for (Map.Entry entry : parameterMap.entrySet()) {
                    if (entry.getValue() != null && entry.getValue() instanceof IOptimisticLock) {
                        return Optional.of((IOptimisticLock) entry.getValue());
                    }
                }
            } else if (parameterObject instanceof IOptimisticLock) {
                return Optional.of((IOptimisticLock) parameterObject);
            }
        }
        return Optional.empty();
    }
}
