package com.tmser.blog.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import com.tmser.blog.model.properties.AliOssProperties;
import com.tmser.blog.model.properties.ApiProperties;
import com.tmser.blog.model.properties.BaiduBosProperties;
import com.tmser.blog.model.properties.EmailProperties;
import com.tmser.blog.model.properties.HuaweiObsProperties;
import com.tmser.blog.model.properties.MinioProperties;
import com.tmser.blog.model.properties.QiniuOssProperties;
import com.tmser.blog.model.properties.SmmsProperties;
import com.tmser.blog.model.properties.TencentCosProperties;
import com.tmser.blog.model.properties.UpOssProperties;
import com.tmser.blog.model.support.HaloConst;
import com.tmser.blog.service.OptionService;

/**
 * Option filter for private options.
 *
 * @author johnniang
 * @date 2021-04-08
 */
public class OptionFilter {

    private final Set<String> defaultPrivateOptionKeys;

    private final OptionService optionService;

    public OptionFilter(OptionService optionService) {
        this.optionService = optionService;
        this.defaultPrivateOptionKeys = getDefaultPrivateOptionKeys();
    }

    private Set<String> getDefaultPrivateOptionKeys() {
        return Sets.newHashSet(
            AliOssProperties.OSS_DOMAIN.getValue(),
            AliOssProperties.OSS_BUCKET_NAME.getValue(),
            AliOssProperties.OSS_ACCESS_KEY.getValue(),
            AliOssProperties.OSS_ACCESS_SECRET.getValue(),

            ApiProperties.API_ACCESS_KEY.getValue(),

            BaiduBosProperties.BOS_DOMAIN.getValue(),
            BaiduBosProperties.BOS_ENDPOINT.getValue(),
            BaiduBosProperties.BOS_BUCKET_NAME.getValue(),
            BaiduBosProperties.BOS_ACCESS_KEY.getValue(),
            BaiduBosProperties.BOS_SECRET_KEY.getValue(),

            EmailProperties.USERNAME.getValue(),
            EmailProperties.PASSWORD.getValue(),
            EmailProperties.FROM_NAME.getValue(),

            HuaweiObsProperties.OSS_DOMAIN.getValue(),
            HuaweiObsProperties.OSS_ENDPOINT.getValue(),
            HuaweiObsProperties.OSS_BUCKET_NAME.getValue(),
            HuaweiObsProperties.OSS_ACCESS_KEY.getValue(),
            HuaweiObsProperties.OSS_ACCESS_SECRET.getValue(),

            MinioProperties.ENDPOINT.getValue(),
            MinioProperties.BUCKET_NAME.getValue(),
            MinioProperties.ACCESS_KEY.getValue(),
            MinioProperties.ACCESS_SECRET.getValue(),

            QiniuOssProperties.OSS_ZONE.getValue(),
            QiniuOssProperties.OSS_ACCESS_KEY.getValue(),
            QiniuOssProperties.OSS_SECRET_KEY.getValue(),
            QiniuOssProperties.OSS_DOMAIN.getValue(),
            QiniuOssProperties.OSS_BUCKET.getValue(),

            SmmsProperties.SMMS_API_SECRET_TOKEN.getValue(),

            TencentCosProperties.COS_DOMAIN.getValue(),
            TencentCosProperties.COS_REGION.getValue(),
            TencentCosProperties.COS_BUCKET_NAME.getValue(),
            TencentCosProperties.COS_SECRET_ID.getValue(),
            TencentCosProperties.COS_SECRET_KEY.getValue(),

            UpOssProperties.OSS_PASSWORD.getValue(),
            UpOssProperties.OSS_BUCKET.getValue(),
            UpOssProperties.OSS_DOMAIN.getValue(),
            UpOssProperties.OSS_OPERATOR.getValue()
        );
    }

    private Set<String> getConfiguredPrivateOptionKeys() {
        // resolve configured private option names
        return Collections.unmodifiableSet(optionService.getByKey(HaloConst.PRIVATE_OPTION_KEY, String.class)
            .map(privateOptions -> privateOptions.split(","))
            .map(arr -> (Set<String>)Sets.newHashSet(arr))
            .orElse(Collections.emptySet())
            .stream()
            .map(String::trim)
            .collect(Collectors.toSet()));
    }

    /**
     * Filter option keys to prevent outsider from accessing private options.
     *
     * @param optionKeys option key collection
     * @return filtered option keys
     */
    public Set<String> filter(Collection<String> optionKeys) {
        if (CollectionUtils.isEmpty(optionKeys)) {
            return Collections.emptySet();
        }

        return Collections.unmodifiableSet(optionKeys.stream()
            .filter(Objects::nonNull)
            .filter(optionKey -> !optionKey.isEmpty())
            .filter(optionKey -> !defaultPrivateOptionKeys.contains(optionKey))
            .filter(optionKey -> !getConfiguredPrivateOptionKeys().contains(optionKey))
            .collect(Collectors.toSet()));
    }

    /**
     * Filter option key to prevent outsider from accessing private option.
     *
     * @param optionKey option key
     * @return an optional of option key
     */
    public Optional<String> filter(String optionKey) {
        if (!StringUtils.hasText(optionKey)) {
            return Optional.empty();
        }
        if (defaultPrivateOptionKeys.contains(optionKey)) {
            return Optional.empty();
        }
        if (getConfiguredPrivateOptionKeys().contains(optionKey)) {
            return Optional.empty();
        }
        return Optional.of(optionKey);
    }
}
