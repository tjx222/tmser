package com.tmser.blog.repository;

import com.tmser.blog.model.entity.ThemeSetting;
import com.tmser.blog.repository.base.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * Theme setting repository interface.
 *
 * @author johnniang
 * @date 4/8/19
 */
@Mapper
public interface ThemeSettingRepository extends BaseRepository<ThemeSetting> {

    /**
     * Finds all theme settings by theme id.
     *
     * @param themeId theme id must not be blank
     * @return a list of theme setting
     */
    @NonNull
    List<ThemeSetting> findAllByThemeId(@NonNull String themeId);

    /**
     * Deletes theme setting by theme id and setting key.
     *
     * @param themeId theme id must not be blank
     * @param key     setting key must not be blank
     * @return affected row(s)
     */
    long deleteByThemeIdAndKey(@NonNull String themeId, @NonNull String key);

    /**
     * Finds theme settings by theme id and setting key.
     *
     * @param themeId theme id must not be blank
     * @param key     setting key must not be blank
     * @return an optional theme setting
     */
    @NonNull
    Optional<ThemeSetting> findByThemeIdAndKey(@NonNull String themeId, @NonNull String key);

    /**
     * Deletes inactivated theme settings.
     *
     * @param activatedThemeId activated theme id.
     */
    void deleteByThemeIdIsNot(@NonNull String activatedThemeId);

    /**
     * Deletes settings by theme id.
     *
     * @param themeId theme id.
     */
    void deleteByThemeId(String themeId);
}
