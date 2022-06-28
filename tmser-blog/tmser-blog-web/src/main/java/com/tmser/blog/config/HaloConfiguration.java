package com.tmser.blog.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import com.tmser.blog.cache.AbstractStringCacheStore;
import com.tmser.blog.cache.InMemoryCacheStore;
import com.tmser.blog.cache.LevelCacheStore;
import com.tmser.blog.config.properties.HaloProperties;
import com.tmser.blog.utils.HttpClientUtils;

/**
 * Halo configuration.
 *
 * @author johnniang
 */
@Slf4j
@EnableAsync
@EnableScheduling
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(HaloProperties.class)
public class HaloConfiguration {

    private final HaloProperties haloProperties;

    public HaloConfiguration(HaloProperties haloProperties) {
        this.haloProperties = haloProperties;
    }

    @Bean
    ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        builder.failOnEmptyBeans(false);
        return builder.build();
    }

    @Bean
    RestTemplate httpsRestTemplate(RestTemplateBuilder builder)
        throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        RestTemplate httpsRestTemplate = builder.build();
        httpsRestTemplate.setRequestFactory(
            new HttpComponentsClientHttpRequestFactory(HttpClientUtils.createHttpsClient(
                (int) haloProperties.getDownloadTimeout().toMillis())));
        return httpsRestTemplate;
    }

    @Bean
    @ConditionalOnMissingBean
    AbstractStringCacheStore stringCacheStore() {
        AbstractStringCacheStore stringCacheStore;
        switch (haloProperties.getCache()) {
            case "level":
                stringCacheStore = new LevelCacheStore(this.haloProperties);
                break;
            case "memory":
            default:
                //memory or default
                stringCacheStore = new InMemoryCacheStore();
                break;
        }
        log.info("Halo cache store load impl : [{}]", stringCacheStore.getClass());
        return stringCacheStore;

    }
}
