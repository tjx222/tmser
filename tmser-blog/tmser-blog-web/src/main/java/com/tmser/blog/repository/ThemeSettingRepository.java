package com.tmser.blog.repository;

import com.tmser.blog.model.entity.ThemeSetting;
import com.tmser.blog.repository.base.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
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
    @Select("select * from theme_settings where theme_id = #{themeId} ")
    List<ThemeSetting> findAllByThemeId(@NonNull String themeId);

    /**
     * Deletes theme setting by theme id and setting key.
     *
     * @param themeId theme id must not be blank
     * @param key     setting key must not be blank
     * @return affected row(s)
     */
    @Update("delete from theme_settings where theme_id = #{themeId} and setting_key=#{key} ")
    long deleteByThemeIdAndKey(@NonNull @Param("themeId") String themeId, @Param("key") @NonNull String key);

    /**
     * Finds theme settings by theme id and setting key.
     *
     * @param themeId theme id must not be blank
     * @param key     setting key must not be blank
     * @return an optional theme setting
     */
    @NonNull
    @Select("select * from theme_settings where theme_id = #{themeId} and setting_key=#{key} ")
    Optional<ThemeSetting> findByThemeIdAndKey(@NonNull @Param("themeId") String themeId, @NonNull @Param("key") String key);

    /**
     * Deletes inactivated theme settings.
     *
     * @param activatedThemeId activated theme id.
     */
    @Update("delete from theme_settings where theme_id <> #{activatedThemeId}")
    void deleteByThemeIdIsNot(@NonNull String activatedThemeId);

    /**
     * Deletes settings by theme id.
     *
     * @param themeId theme id.
     */
    @Update("delete from theme_settings where theme_id = #{themeId}")
    void deleteByThemeId(@Param("themeId") String themeId);
}
