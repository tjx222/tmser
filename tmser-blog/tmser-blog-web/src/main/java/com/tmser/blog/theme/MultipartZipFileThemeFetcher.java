package com.tmser.blog.theme;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.zip.ZipInputStream;

import com.tmser.blog.exception.ServiceException;
import com.tmser.blog.exception.ThemePropertyMissingException;
import com.tmser.blog.handler.theme.config.support.ThemeProperty;
import com.tmser.blog.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import static com.tmser.blog.utils.FileUtils.unzip;

/**
 * Multipart zip file theme fetcher.
 *
 * @author johnniang
 */
@Slf4j
public class MultipartZipFileThemeFetcher implements ThemeFetcher {

    @Override
    public boolean support(Object source) {
        if (source instanceof MultipartFile) {
            final String filename = ((MultipartFile) source).getOriginalFilename();
            return filename != null && filename.endsWith(".zip");
        }
        return false;
    }

    @Override
    public ThemeProperty fetch(Object source) {
        final MultipartFile file = (MultipartFile) source;

        try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {
            final Path tempDirectory = FileUtils.createTempDirectory();
            log.info("Unzipping {} to path {}", file.getOriginalFilename(), tempDirectory);
            unzip(zis, tempDirectory);
            return ThemePropertyScanner.INSTANCE.fetchThemeProperty(tempDirectory)
                    .orElseThrow(() -> new ThemePropertyMissingException("主题配置文件缺失！请确认后重试。"));
        } catch (IOException e) {
            throw new ServiceException("主题上传失败！", e);
        }
    }

}
