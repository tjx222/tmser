package com.tmser.config;

import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.tmser.database.mybatis.*;
import com.tmser.sensitive.AbstractSensitiveProcess;
import com.tmser.sensitive.AesSensitiveProcessor;
import com.tmser.sensitive.SensitiveProcessor;
import com.tmser.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MybatisConfig {

    @Value("${mybaits-plus.pagination.max-limit:1000}")
    private Long maxLimit = 1000L;

    @Bean
    @ConditionalOnMissingBean(PaginationInterceptor.class)
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // 设置请求的页面大于最大页后操作， true调回到首页，false 继续请求  默认false
        // paginationInterceptor.setOverflow(false);
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        paginationInterceptor.setMaxLimit(maxLimit);
        // 开启 count 的 join 优化,只针对部分 left join
        paginationInterceptor.setOptimizeJoin(true);
        return paginationInterceptor;
    }

    /**
     * 乐观锁
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(OptimisticLockerInnerInterceptor.class)
    @ConditionalOnProperty(matchIfMissing = true, name = "mybatis-plus.optimisticLock.disable", havingValue = "false")
    public OptimisticLockerInnerInterceptor optimisticLockInterceptor() {
        return new OptimisticLockerInnerInterceptor();
    }


    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(List<InnerInterceptor> innerInterceptorList) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        if (CollectionUtils.isNotEmpty(innerInterceptorList)) {
            interceptor.setInterceptors(innerInterceptorList);
        }
        //防全表更新或删除
        //interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        return interceptor;
    }


    @Bean
    @ConditionalOnMissingBean(AbstractSensitiveProcess.class)
    @ConditionalOnProperty(name = "mybatis-plus.sensitive.enable")
    public SensitiveProcessor sensitiveProcessor() {
        return new AesSensitiveProcessor();
    }

    @Bean
    @ConditionalOnProperty(name = "mybatis-plus.sensitive.enable")
    public SensitiveFiledInterceptor sensitiveFiledInterceptor() {
        return new SensitiveFiledInterceptor();
    }

    @Bean
    public JpaFieldAutoFillProcessor modifyTimeAutoFillProcessor() {
        return new JpaFieldAutoFillProcessor();
    }

    @Bean
    public VersionAutoFillProcessor versionAutoFillProcessor() {
        return new VersionAutoFillProcessor();
    }

    /**
     * 乐观锁
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(AutoFillInterceptor.class)
    @ConditionalOnProperty(matchIfMissing = true, name = "mybatis-plus.autofill.disable", havingValue = "false")
    public AutoFillInterceptor autoFillInterceptor() {
        return new AutoFillInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean(ISqlInjector.class)
    public BizIdLogicSqlInjector bizIdLogicSqlInjector() {
        return new BizIdLogicSqlInjector();
    }

}
