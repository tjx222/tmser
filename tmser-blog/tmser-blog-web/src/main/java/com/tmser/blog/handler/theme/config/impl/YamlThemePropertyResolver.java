package com.tmser.blog.handler.theme.config.impl;

import java.io.IOException;

import com.tmser.blog.handler.theme.config.ThemePropertyResolver;
import com.tmser.blog.handler.theme.config.support.ThemeProperty;
import com.tmser.blog.theme.YamlResolver;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Yaml theme file resolver.
 *
 * @author johnniang
 * @date 2019-04-11
 */
@Service
public class YamlThemePropertyResolver implements ThemePropertyResolver {

    @Override
    @NonNull
    public ThemeProperty resolve(@NonNull String content) throws IOException {
        Assert.hasText(content, "Theme file content must not be null");

        return YamlResolver.INSTANCE.getYamlMapper().readValue(content, ThemeProperty.class);
    }
}
