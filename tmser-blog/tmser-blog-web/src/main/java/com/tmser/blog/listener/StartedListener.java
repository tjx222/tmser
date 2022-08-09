package com.tmser.blog.listener;

import com.tmser.blog.config.properties.HaloProperties;
import com.tmser.blog.handler.theme.config.support.ThemeProperty;
import com.tmser.blog.model.properties.PrimaryProperties;
import com.tmser.blog.model.support.HaloConst;
import com.tmser.blog.service.OptionService;
import com.tmser.blog.service.ThemeService;
import com.tmser.blog.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;
import org.eclipse.jgit.storage.file.WindowCacheConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.Collections;
import java.util.Optional;

/**
 * The method executed after the application is started.
 *
 * @author ryanwang
 * @author guqing
 * @date 2018-12-05
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StartedListener implements ApplicationListener<ApplicationStartedEvent> {

    @Autowired
    private HaloProperties haloProperties;

    @Autowired
    private OptionService optionService;

    @Autowired
    private ThemeService themeService;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        this.initDirectory();
        //this.initThemes();
        this.printStartInfo();
       // this.configGit();
    }

    private void configGit() {
        // Config packed git MMAP
        if (SystemUtils.IS_OS_WINDOWS) {
            WindowCacheConfig config = new WindowCacheConfig();
            config.setPackedGitMMAP(false);
            config.install();
        }
    }

    private void printStartInfo() {
        String blogUrl = optionService.getBlogBaseUrl();
        log.info(AnsiOutput.toString(AnsiColor.BRIGHT_BLUE, "Tmser started at         ", blogUrl));
        log.info(AnsiOutput
                .toString(AnsiColor.BRIGHT_BLUE, "Tmser admin started at   ", blogUrl, "/",
                        haloProperties.getAdminPath()));
        log.info(AnsiOutput.toString(AnsiColor.BRIGHT_YELLOW, "Tmser has started successfully!"));
    }

    /**
     * Init internal themes.
     */
    private void initThemes() {
        // Whether the blog has initialized
        Boolean isInstalled = optionService
                .getByPropertyOrDefault(PrimaryProperties.IS_INSTALLED, Boolean.class, false);

        try {
            String themeClassPath = ResourceUtils.CLASSPATH_URL_PREFIX + ThemeService.THEME_FOLDER;

            URI themeUri = ResourceUtils.getURL(themeClassPath).toURI();

            log.debug("Theme uri: [{}]", themeUri);

            Path source;

            if ("jar".equalsIgnoreCase(themeUri.getScheme())) {

                // Create new file system for jar
                FileSystem fileSystem = getFileSystem(themeUri);
                source = fileSystem.getPath("/BOOT-INF/classes/" + ThemeService.THEME_FOLDER);
            } else {
                source = Paths.get(themeUri);
            }

            // Create theme folder
            Path themePath = themeService.getBasePath().resolve(HaloConst.DEFAULT_THEME_ID);
            Optional<ThemeProperty> themeProperty = themeService.fetchThemePropertyBy(HaloConst.DEFAULT_THEME_ID);
            if (!themeProperty.isPresent()) {
                FileUtils.copyFolder(source.resolve(HaloConst.DEFAULT_THEME_DIR_NAME), themePath);
                log.info("Copied theme folder from [{}] to [{}]", source, themePath);
            }
        } catch (Exception e) {
            if (e instanceof FileNotFoundException) {
                log.error("Please check location: classpath:{}", ThemeService.THEME_FOLDER);
            }
            log.error("Initialize internal theme to user path error!", e);
        }
    }

    @NonNull
    private FileSystem getFileSystem(@NonNull URI uri) throws IOException {
        Assert.notNull(uri, "Uri must not be null");

        FileSystem fileSystem;

        try {
            fileSystem = FileSystems.getFileSystem(uri);
        } catch (FileSystemNotFoundException e) {
            fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
        }

        return fileSystem;
    }

    private void initDirectory() {
        Path workPath = Paths.get(haloProperties.getWorkDir());
        Path backupPath = Paths.get(haloProperties.getBackupDir());
        Path dataExportPath = Paths.get(haloProperties.getDataExportDir());

        try {
            if (Files.notExists(workPath)) {
                Files.createDirectories(workPath);
                log.info("Created work directory: [{}]", workPath);
            }

            if (Files.notExists(backupPath)) {
                Files.createDirectories(backupPath);
                log.info("Created backup directory: [{}]", backupPath);
            }

            if (Files.notExists(dataExportPath)) {
                Files.createDirectories(dataExportPath);
                log.info("Created data export directory: [{}]", dataExportPath);
            }
        } catch (IOException ie) {
            throw new RuntimeException("Failed to initialize directories", ie);
        }
    }
}
