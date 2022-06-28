package com.tmser.blog.listener.theme;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.tmser.blog.cache.AbstractStringCacheStore;
import com.tmser.blog.event.options.OptionUpdatedEvent;
import com.tmser.blog.event.theme.ThemeUpdatedEvent;
import com.tmser.blog.service.ThemeService;

/**
 * Theme updated listener.
 *
 * @author johnniang
 * @date 19-4-29
 */
@Component
public class ThemeUpdatedListener {

    private final AbstractStringCacheStore cacheStore;

    public ThemeUpdatedListener(AbstractStringCacheStore cacheStore) {
        this.cacheStore = cacheStore;
    }

    @EventListener
    public void onApplicationEvent(ThemeUpdatedEvent event) {
        cacheStore.delete(ThemeService.THEMES_CACHE_KEY);
    }

    @EventListener
    public void onOptionUpdatedEvent(OptionUpdatedEvent optionUpdatedEvent) {
        cacheStore.delete(ThemeService.THEMES_CACHE_KEY);
    }
}
