package com.tmser.database.mybatis;

import com.tmser.util.CollectionUtils;
import lombok.Setter;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;

/**
 * 自动填充拦截器
 * <p>
 * 插入时如果没有填充，对乐观版本进行初始值填充
 * 更新时更新时间填充为当前时间。
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}
        )})
public class AutoFillInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(AutoFillInterceptor.class);

    @Setter
    @Resource
    private List<AutoFillProcessor> autoFillProcessors;

    protected void beforeUpdate(Executor executor, MappedStatement ms, Object parameter) throws SQLException {
        if ((SqlCommandType.UPDATE == ms.getSqlCommandType()
                || SqlCommandType.INSERT == ms.getSqlCommandType())
                && CollectionUtils.isNotEmpty(autoFillProcessors)
        ) {
            logger.debug("start auto fill, parameter:{} autoFillProcessors:{}", parameter, autoFillProcessors);
            autoFillProcessors.forEach(e -> e.processFiled(ms.getId(), parameter, ms.getSqlCommandType()));
        }
    }


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        Object[] args = invocation.getArgs();
        if (target instanceof Executor) {
            final Executor executor = (Executor) target;
            Object parameter = args[1];
            MappedStatement ms = (MappedStatement) args[0];
            beforeUpdate(executor, ms, parameter);
        }
        return invocation.proceed();
    }
}
