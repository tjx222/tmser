package com.tmser.blog.theme;

import com.tmser.blog.exception.BadRequestException;
import com.tmser.blog.exception.NotFoundException;
import com.tmser.blog.handler.theme.config.support.ThemeProperty;
import com.tmser.blog.repository.ThemeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Multipart file theme updater.
 *
 * @author johnniang
 */
@Slf4j
public class MultipartFileThemeUpdater implements ThemeUpdater {

    private final MultipartFile file;

    private final ThemeFetcherComposite fetcherComposite;

    private final ThemeRepository themeRepository;

    public MultipartFileThemeUpdater(MultipartFile file,
                                     ThemeFetcherComposite fetcherComposite,
                                     ThemeRepository themeRepository) {
        this.file = file;
        this.fetcherComposite = fetcherComposite;
        this.themeRepository = themeRepository;
    }

    @Override
    public ThemeProperty update(String themeId) throws IOException {
        // check old theme id
        final ThemeProperty oldThemeProperty = this.themeRepository.fetchThemePropertyByThemeId(themeId)
                .orElseThrow(() -> new NotFoundException("主题 ID 为 " + themeId + " 不存在或已删除！"));

        // fetch new theme
        final ThemeProperty newThemeProperty = this.fetcherComposite.fetch(this.file);

        if (!Objects.equals(oldThemeProperty.getId(), newThemeProperty.getId())) {
            log.error("Expected theme: {}, but provided theme: {}",
                    oldThemeProperty.getId(),
                    newThemeProperty.getId());
            // clear new theme folder
            this.themeRepository.deleteTheme(newThemeProperty);
            throw new BadRequestException("上传的主题 "
                    + newThemeProperty.getId()
                    + " 和当前主题的 "
                    + oldThemeProperty.getId()
                    + " 不一致，无法进行更新操作！");
        }

        // backup old theme
        final Path backupPath = ThemeUpdater.backup(oldThemeProperty);

        try {
            // delete  old theme
            themeRepository.deleteTheme(oldThemeProperty);

            // add new theme
            return themeRepository.attemptToAdd(newThemeProperty);
        } catch (Throwable t) {
            log.error("Failed to add new theme, and restoring old theme from " + backupPath, t);
            ThemeUpdater.restore(backupPath, oldThemeProperty);
            log.info("Restored old theme from path: {}", backupPath);
            throw t;
        }
    }

}
