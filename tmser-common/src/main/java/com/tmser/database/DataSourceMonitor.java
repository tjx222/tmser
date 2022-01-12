package com.tmser.database;

import com.alibaba.druid.pool.DruidDataSource;
import com.zaxxer.hikari.HikariConfigMXBean;
import com.zaxxer.hikari.HikariPoolMXBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 数据源执行情况监控
 * modify history:
 */
//@Component
@Slf4j
public class DataSourceMonitor implements InitializingBean {

    @Resource
    private AbstractRoutingDataSource dynamicDataSource;

    @Override
    public void afterPropertiesSet() throws Exception {
        Timer timer = new Timer(true);
        TimerTask monitorTask = new MonitorTask();
        timer.schedule(monitorTask, 60 * 1000L, 10 * 1000L);
    }

    class MonitorTask extends TimerTask {
        @Override
        public void run() {
            // TODO 增加实际监控代码
        }
    }

    private Map<String, Object> getHikariStatMap(HikariPoolMXBean mxBean, HikariConfigMXBean configMXBean) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            // 0 - 4
            map.put("ActiveConnections", mxBean.getActiveConnections());
            map.put("IdleConnections", mxBean.getIdleConnections());
            map.put("ThreadsAwaitingConnection", mxBean.getThreadsAwaitingConnection());
            map.put("TotalConnections", mxBean.getTotalConnections());
            // 5 - 9
            map.put("MaximumPoolSize", configMXBean.getMaximumPoolSize());
            map.put("MinimumIdle", configMXBean.getMinimumIdle());
        } catch (Exception ex) {
            log.warn("getStatData error", ex);
        }
        return map;
    }

    private Map<String, Object> getStatMap(DruidDataSource dataSource) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            // 0 - 4
            map.put("CreateCount", dataSource.getCreateCount());
            map.put("DestroyCount", dataSource.getDestroyCount());
            map.put("ConnectCount", dataSource.getConnectCount());
            map.put("ConnectErrorCount", dataSource.getCreateErrorCount());
            // 5 - 9
            map.put("CloseCount", dataSource.getCloseCount());
            map.put("ActiveCount", dataSource.getActiveCount());
            map.put("ActivePeak", dataSource.getActivePeak());
            map.put("ActivePeakTime", dataSource.getActivePeakTime());
            map.put("PoolingCount", dataSource.getPoolingCount());
            map.put("PoolingPeak", dataSource.getPoolingPeak());
            map.put("PoolingPeakTime", dataSource.getPoolingPeakTime());
            map.put("LockQueueLength", dataSource.getLockQueueLength());
            map.put("WaitThreadCount", dataSource.getNotEmptyWaitThreadCount());

            // 10 - 14
            map.put("InitialSize", dataSource.getInitialSize());
            map.put("MaxActive", dataSource.getMaxActive());
            map.put("MinIdle", dataSource.getMinIdle());
            map.put("PoolPreparedStatements", dataSource.isPoolPreparedStatements());
            map.put("TestOnBorrow", dataSource.isTestOnBorrow());

            map.put("QueryTimeout", dataSource.getQueryTimeout());

            map.put("ExecuteCount", dataSource.getExecuteCount());
            map.put("ExecuteUpdateCount", dataSource.getExecuteUpdateCount());
            map.put("ExecuteQueryCount", dataSource.getExecuteQueryCount());
            map.put("ExecuteBatchCount", dataSource.getExecuteBatchCount());
            map.put("ErrorCount", dataSource.getErrorCount());
            map.put("CommitCount", dataSource.getCommitCount());
            map.put("RollbackCount", dataSource.getRollbackCount());

            map.put("PSCacheAccessCount", dataSource.getCachedPreparedStatementAccessCount());
            map.put("PSCacheHitCount", dataSource.getCachedPreparedStatementHitCount());
            map.put("PSCacheMissCount", dataSource.getCachedPreparedStatementMissCount());
        } catch (Exception ex) {
            log.warn("getStatData error", ex);
        }
        return map;
    }
}
