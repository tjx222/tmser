package com.tmser.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.tmser.database.mybatis.OptimisticLockInterceptor;
import com.tmser.database.mybatis.SensitiveFiledInterceptor;
import com.tmser.sensitive.AbstractSensitiveProcess;
import com.tmser.sensitive.AesSensitiveProcessor;
import com.tmser.sensitive.SensitiveProcessor;
import com.tmser.util.CollectionUtils;
import com.tmser.util.ConfigUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MybatisConfig {

    @Bean
    @ConditionalOnMissingBean(PaginationInnerInterceptor.class)
    public PaginationInnerInterceptor paginationInterceptor() {
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
        // 设置请求的页面大于最大页后操作， true调回到首页，false 继续请求  默认false
        // paginationInterceptor.setOverflow(false);
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        paginationInterceptor.setMaxLimit(ConfigUtils.getLong("mybaits-plus","pagination.max-limit", 1000L));
        // 开启 count 的 join 优化,只针对部分 left join
        paginationInterceptor.setOptimizeJoin(true);
        return paginationInterceptor;
    }

    /**
     * 乐观锁
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(OptimisticLockInterceptor.class)
    @ConditionalOnProperty(matchIfMissing=true, name="mybaits-plus.optimisticLock.disable", havingValue = "false")
    public OptimisticLockInterceptor optimisticLockInterceptor() {
        return new OptimisticLockInterceptor();
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
    @ConditionalOnProperty(name="mybaits-plus.sensitive.enable")
    public SensitiveProcessor sensitiveProcessor(){
        return new AesSensitiveProcessor();
    }

    @Bean
    @ConditionalOnProperty(name="mybaits-plus.sensitive.enable")
    public SensitiveFiledInterceptor sensitiveFiledInterceptor() {
        return new SensitiveFiledInterceptor();
    }
}
