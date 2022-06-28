package com.tmser.blog.controller.content.api;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tmser.blog.handler.theme.config.support.ThemeProperty;
import com.tmser.blog.service.ThemeService;
import com.tmser.blog.service.ThemeSettingService;

/**
 * Content theme controller.
 *
 * @author ryanwang
 * @date 2020-01-17
 */
@RestController("ApiContentThemeController")
@RequestMapping("/api/content/themes")
public class ThemeController {

    private final ThemeService themeService;

    private final ThemeSettingService themeSettingService;

    public ThemeController(ThemeService themeService, ThemeSettingService themeSettingService) {
        this.themeService = themeService;
        this.themeSettingService = themeSettingService;
    }

    @GetMapping("activation")
    public ThemeProperty getBy() {
        return themeService.getThemeOfNonNullBy(themeService.getActivatedThemeId());
    }

    @GetMapping("{themeId:.+}")
    public ThemeProperty getBy(@PathVariable("themeId") String themeId) {
        return themeService.getThemeOfNonNullBy(themeId);
    }

    @GetMapping("activation/settings")
    public Map<String, Object> listSettingsBy() {
        return themeSettingService.listAsMapBy(themeService.getActivatedThemeId());
    }

    @GetMapping("{themeId:.+}/settings")
    public Map<String, Object> listSettingsBy(@PathVariable("themeId") String themeId) {
        return themeSettingService.listAsMapBy(themeId);
    }
}
