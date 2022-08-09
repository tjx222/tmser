package com.tmser.blog.service.impl;

import static com.tmser.blog.model.support.HaloConst.HALO_BACKUP_MARKDOWN_PREFIX;
import static com.tmser.blog.model.support.HaloConst.HALO_BACKUP_PREFIX;
import static com.tmser.blog.model.support.HaloConst.HALO_DATA_EXPORT_PREFIX;
import static com.tmser.blog.utils.DateTimeUtils.HORIZONTAL_LINE_DATETIME_FORMATTER;
import static com.tmser.blog.utils.FileUtils.checkDirectoryTraversal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipOutputStream;

import com.tmser.blog.model.entity.*;
import com.tmser.blog.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import com.tmser.blog.config.properties.HaloProperties;
import com.tmser.blog.event.options.OptionUpdatedEvent;
import com.tmser.blog.event.theme.ThemeUpdatedEvent;
import com.tmser.blog.exception.BadRequestException;
import com.tmser.blog.exception.NotFoundException;
import com.tmser.blog.exception.ServiceException;
import com.tmser.blog.handler.file.FileHandler;
import com.tmser.blog.model.dto.BackupDTO;
import com.tmser.blog.model.dto.post.BasePostDetailDTO;
import com.tmser.blog.model.params.PostMarkdownParam;
import com.tmser.blog.model.support.HaloConst;
import com.tmser.blog.model.vo.PostMarkdownVO;
import com.tmser.blog.security.service.OneTimeTokenService;
import com.tmser.blog.utils.DateTimeUtils;
import com.tmser.blog.utils.DateUtils;
import com.tmser.blog.utils.FileUtils;
import com.tmser.blog.utils.HaloUtils;
import com.tmser.blog.utils.JsonUtils;

/**
 * Backup service implementation.
 *
 * @author johnniang
 * @author ryanwang
 * @author Raremaa
 * @author guqing
 * @date  2019-04-26
 */
@Service
@Slf4j
public class BackupServiceImpl implements BackupService {

    private static final String BACKUP_RESOURCE_BASE_URI = "/api/admin/backups/work-dir";

    private static final String DATA_EXPORT_MARKDOWN_BASE_URI =
        "/api/admin/backups/markdown/export";

    private static final String DATA_EXPORT_BASE_URI = "/api/admin/backups/data";

    private static final String UPLOAD_SUB_DIR = "upload/";

    private final VisitLogService visitLogService;

    private final ShareInfoService shareInfoService;

    private final AttachmentService attachmentService;

    private final LogService logService;

    private final OptionService optionService;

    private final UserService userService;

    private final OneTimeTokenService oneTimeTokenService;

    private final HaloProperties haloProperties;

    private final ApplicationEventPublisher eventPublisher;

    public BackupServiceImpl(AttachmentService attachmentService, VisitLogService visitLogService,
                             ShareInfoService shareInfoService, LogService logService,
         OptionService optionService, UserService userService,
        OneTimeTokenService oneTimeTokenService, HaloProperties haloProperties,
        ApplicationEventPublisher eventPublisher) {
        this.attachmentService = attachmentService;
        this.shareInfoService = shareInfoService;
        this.visitLogService = visitLogService;
        this.logService = logService;
        this.optionService = optionService;
        this.userService = userService;
        this.oneTimeTokenService = oneTimeTokenService;
        this.haloProperties = haloProperties;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public BasePostDetailDTO importMarkdown(MultipartFile file) throws IOException {

        // Read markdown content.
        String markdown = FileUtils.readString(file.getInputStream());

        // TODO sheet import
        return null;
    }

    @Override
    public BackupDTO backupWorkDirectory(List<String> options) {
        if (CollectionUtils.isEmpty(options)) {
            throw new BadRequestException("The options parameter is missing, at least one.");
        }
        // Zip work directory to temporary file
        try {
            // Create zip path for halo zip
            String haloZipFileName = HALO_BACKUP_PREFIX
                + DateTimeUtils.format(LocalDateTime.now(), HORIZONTAL_LINE_DATETIME_FORMATTER)
                + HaloUtils.simpleUUID().hashCode() + ".zip";
            // Create halo zip file
            Path haloZipFilePath = Paths.get(haloProperties.getBackupDir(), haloZipFileName);
            if (!Files.exists(haloZipFilePath.getParent())) {
                Files.createDirectories(haloZipFilePath.getParent());
            }
            Path haloZipPath = Files.createFile(haloZipFilePath);

            // Zip halo
            com.tmser.blog.utils.FileUtils
                .zip(Paths.get(this.haloProperties.getWorkDir()), haloZipPath,
                    path -> {
                        for (String itemToBackup : options) {
                            Path backupItemPath =
                                Paths.get(this.haloProperties.getWorkDir()).resolve(itemToBackup);
                            if (path.startsWith(backupItemPath)) {
                                return true;
                            }
                        }
                        return false;
                    });

            // Build backup dto
            return buildBackupDto(BACKUP_RESOURCE_BASE_URI, haloZipPath);
        } catch (IOException e) {
            throw new ServiceException("Failed to backup halo", e);
        }
    }

    @Override
    public List<BackupDTO> listWorkDirBackups() {
        // Ensure the parent folder exist
        Path backupParentPath = Paths.get(haloProperties.getBackupDir());
        if (Files.notExists(backupParentPath)) {
            return Collections.emptyList();
        }

        // Build backup dto
        try (Stream<Path> subPathStream = Files.list(backupParentPath)) {
            return subPathStream
                .filter(backupPath -> StringUtils
                    .startsWithIgnoreCase(backupPath.getFileName().toString(),
                        HALO_BACKUP_PREFIX))
                .map(backupPath -> buildBackupDto(BACKUP_RESOURCE_BASE_URI, backupPath))
                .sorted(Comparator.comparingLong(BackupDTO::getUpdateTime).reversed())
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw new ServiceException("Failed to fetch backups", e);
        }
    }

    @Override
    public Optional<BackupDTO> getBackup(@NonNull Path backupFilePath, @NonNull BackupType type) {
        if (Files.notExists(backupFilePath)) {
            return Optional.empty();
        }

        BackupDTO backupDto = buildBackupDto(type.getBaseUri(), backupFilePath);
        return Optional.of(backupDto);
    }

    @Override
    public void deleteWorkDirBackup(String fileName) {
        Assert.hasText(fileName, "File name must not be blank");

        Path backupRootPath = Paths.get(haloProperties.getBackupDir());

        // Get backup path
        Path backupPath = backupRootPath.resolve(fileName);

        // Check directory traversal
        checkDirectoryTraversal(backupRootPath, backupPath);

        try {
            // Delete backup file
            Files.delete(backupPath);
        } catch (NoSuchFileException e) {
            throw new NotFoundException("The file " + fileName + " was not found", e);
        } catch (IOException e) {
            throw new ServiceException("Failed to delete backup", e);
        }
    }

    @Override
    public Resource loadFileAsResource(String basePath, String fileName) {
        Assert.hasText(basePath, "Base path must not be blank");
        Assert.hasText(fileName, "Backup file name must not be blank");

        Path backupParentPath = Paths.get(basePath);

        try {
            if (Files.notExists(backupParentPath)) {
                // Create backup parent path if it does not exists
                Files.createDirectories(backupParentPath);
            }

            // Get backup file path
            Path backupFilePath = Paths.get(basePath, fileName).normalize();

            // Check directory traversal
            checkDirectoryTraversal(backupParentPath, backupFilePath);

            // Build url resource
            Resource backupResource = new UrlResource(backupFilePath.toUri());
            if (!backupResource.exists()) {
                // If the backup resource is not exist
                throw new NotFoundException("The file " + fileName + " was not found");
            }
            // Return the backup resource
            return backupResource;
        } catch (MalformedURLException e) {
            throw new NotFoundException("The file " + fileName + " was not found", e);
        } catch (IOException e) {
            throw new ServiceException("Failed to create backup parent path: " + backupParentPath,
                e);
        }
    }

    @Override
    public BackupDTO exportData() {
        Map<String, Object> data = new HashMap<>();
        data.put("version", HaloConst.HALO_VERSION);
        data.put("export_date", DateUtils.now());
        data.put("attachments", attachmentService.listAll());
//        data.put("categories", categoryService.listAll(true));
//        data.put("comment_black_list", commentBlackListService.listAll());
//        data.put("journals", journalService.listAll());
//        data.put("journal_comments", journalCommentService.listAll());
//        data.put("links", linkService.listAll());
        data.put("logs", logService.listAll());
        data.put("visitLogs", visitLogService.listAll());
        data.put("shareInfos", shareInfoService.listAll());
 //       data.put("menus", menuService.listAll());
        data.put("options", optionService.listAll());
//        data.put("photos", photoService.listAll());
//        data.put("posts", postService.listAll());
//        data.put("post_categories", postCategoryService.listAll());
//        data.put("post_comments", postCommentService.listAll());
//        data.put("post_metas", postMetaService.listAll());
//        data.put("post_tags", postTagService.listAll());
//        data.put("sheets", sheetService.listAll());
//        data.put("sheet_comments", sheetCommentService.listAll());
//        data.put("sheet_metas", sheetMetaService.listAll());
//        data.put("tags", tagService.listAll());
       // data.put("theme_settings", themeSettingService.listAll());
        data.put("user", userService.listAll());

        try {
            String haloDataFileName = HALO_DATA_EXPORT_PREFIX
                + DateTimeUtils.format(LocalDateTime.now(), HORIZONTAL_LINE_DATETIME_FORMATTER)
                + HaloUtils.simpleUUID().hashCode() + ".json";

            Path haloDataFilePath = Paths.get(haloProperties.getDataExportDir(), haloDataFileName);
            if (!Files.exists(haloDataFilePath.getParent())) {
                Files.createDirectories(haloDataFilePath.getParent());
            }
            Path haloDataPath = Files.createFile(haloDataFilePath);

            FileUtils.writeStringToFile(haloDataPath.toFile(), JsonUtils.objectToJson(data));
            return buildBackupDto(DATA_EXPORT_BASE_URI, haloDataPath);
        } catch (IOException e) {
            throw new ServiceException("导出数据失败", e);
        }
    }

    @Override
    public List<BackupDTO> listExportedData() {

        Path exportedDataParentPath = Paths.get(haloProperties.getDataExportDir());
        if (Files.notExists(exportedDataParentPath)) {
            return Collections.emptyList();
        }

        try (Stream<Path> subPathStream = Files.list(exportedDataParentPath)) {
            return subPathStream
                .filter(backupPath -> StringUtils
                    .startsWithIgnoreCase(backupPath.getFileName().toString(),
                        HALO_DATA_EXPORT_PREFIX))
                .map(backupPath -> buildBackupDto(DATA_EXPORT_BASE_URI, backupPath))
                .sorted(Comparator.comparingLong(BackupDTO::getUpdateTime).reversed())
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw new ServiceException("Failed to fetch exported data", e);
        }
    }

    @Override
    public void deleteExportedData(String fileName) {
        Assert.hasText(fileName, "File name must not be blank");

        Path dataExportRootPath = Paths.get(haloProperties.getDataExportDir());

        Path backupPath = dataExportRootPath.resolve(fileName);

        checkDirectoryTraversal(dataExportRootPath, backupPath);

        try {
            // Delete backup file
            Files.delete(backupPath);
        } catch (NoSuchFileException e) {
            throw new NotFoundException("The file " + fileName + " was not found", e);
        } catch (IOException e) {
            throw new ServiceException("Failed to delete backup", e);
        }
    }

    @Override
    public void importData(MultipartFile file) throws IOException {
        String jsonContent = FileUtils.readString(file.getInputStream());

        ObjectMapper mapper = JsonUtils.createDefaultJsonMapper();
        TypeReference<HashMap<String, Object>> typeRef =
            new TypeReference() {
            };
        HashMap<String, Object> data = mapper.readValue(jsonContent, typeRef);

        List<Attachment> attachments = Arrays.asList(mapper
            .readValue(mapper.writeValueAsString(data.get("attachments")), Attachment[].class));
        attachmentService.createInBatch(attachments);

        List<ShareInfo> shareInfos = Arrays.asList(mapper
                .readValue(mapper.writeValueAsString(data.get("shareInfos")), ShareInfo[].class));
        shareInfoService.createInBatch(shareInfos);

        List<VisitLog> visitLogs = Arrays.asList(mapper
                .readValue(mapper.writeValueAsString(data.get("visitLogs")), VisitLog[].class));
        visitLogService.createInBatch(visitLogs);

        List<Log> logs = Arrays
            .asList(mapper.readValue(mapper.writeValueAsString(data.get("logs")), Log[].class));
        logService.createInBatch(logs);

        List<Option> options = Arrays.asList(
            mapper.readValue(mapper.writeValueAsString(data.get("options")), Option[].class));
        optionService.createInBatch(options);

        eventPublisher.publishEvent(new OptionUpdatedEvent(this));

        eventPublisher.publishEvent(new ThemeUpdatedEvent(this));

        List<User> users = Arrays.asList(mapper
            .readValue(mapper.writeValueAsString(data.get("user")),
                User[].class));

        if (users.size() > 0) {
            userService.create(users.get(0));
        }
    }

    @Override
    public BackupDTO exportMarkdowns(PostMarkdownParam postMarkdownParam) throws IOException {
        // Query all Post data
        List<PostMarkdownVO> postMarkdownList = Collections.emptyList();
        Assert.notEmpty(postMarkdownList, "当前无文章可以导出");

        // Write files to the temporary directory
        String markdownFileTempPathName =
            haloProperties.getBackupMarkdownDir() + HaloUtils.simpleUUID().hashCode();
        for (PostMarkdownVO postMarkdownVo : postMarkdownList) {
            StringBuilder content = new StringBuilder();
            Boolean needFrontMatter =
                Optional.ofNullable(postMarkdownParam.getNeedFrontMatter()).orElse(false);
            if (needFrontMatter) {
                // Add front-matter
                content.append(postMarkdownVo.getFrontMatter()).append("\n");
            }
            content.append(postMarkdownVo.getOriginalContent());
            try {
                String markdownFileName =
                    postMarkdownVo.getTitle() + "-" + postMarkdownVo.getSlug() + ".md";
                Path markdownFilePath = Paths.get(markdownFileTempPathName, markdownFileName);
                if (!Files.exists(markdownFilePath.getParent())) {
                    Files.createDirectories(markdownFilePath.getParent());
                }
                Path markdownDataPath = Files.createFile(markdownFilePath);
                FileUtils.writeStringToFile(markdownDataPath.toFile(), content.toString());
            } catch (IOException e) {
                throw new ServiceException("导出数据失败", e);
            }
        }

        // Create zip path
        String markdownZipFileName = HALO_BACKUP_MARKDOWN_PREFIX
            + DateTimeUtils.format(LocalDateTime.now(), HORIZONTAL_LINE_DATETIME_FORMATTER)
            + HaloUtils.simpleUUID().hashCode() + ".zip";

        // Create zip file
        Path markdownZipFilePath =
            Paths.get(haloProperties.getBackupMarkdownDir(), markdownZipFileName);
        if (!Files.exists(markdownZipFilePath.getParent())) {
            Files.createDirectories(markdownZipFilePath.getParent());
        }
        Path markdownZipPath = Files.createFile(markdownZipFilePath);
        // Zip file
        try (ZipOutputStream markdownZipOut = new ZipOutputStream(
            Files.newOutputStream(markdownZipPath))) {

            // Zip temporary directory
            Path markdownFileTempPath = Paths.get(markdownFileTempPathName);
            com.tmser.blog.utils.FileUtils.zip(markdownFileTempPath, markdownZipOut);

            // Zip upload sub-directory
            String uploadPathName =
                FileHandler.normalizeDirectory(haloProperties.getWorkDir()) + UPLOAD_SUB_DIR;
            Path uploadPath = Paths.get(uploadPathName);
            if (Files.exists(uploadPath)) {
                com.tmser.blog.utils.FileUtils.zip(uploadPath, markdownZipOut);
            }

            // Remove files in the temporary directory
            com.tmser.blog.utils.FileUtils.deleteFolder(markdownFileTempPath);

            // Build backup dto
            return buildBackupDto(DATA_EXPORT_MARKDOWN_BASE_URI, markdownZipPath);
        } catch (IOException e) {
            throw new ServiceException("Failed to export markdowns", e);
        }
    }

    @Override
    public List<BackupDTO> listMarkdowns() {
        // Ensure the parent folder exist
        Path backupParentPath = Paths.get(haloProperties.getBackupMarkdownDir());
        if (Files.notExists(backupParentPath)) {
            return Collections.emptyList();
        }

        // Build backup dto
        try (Stream<Path> subPathStream = Files.list(backupParentPath)) {
            return subPathStream
                .filter(backupPath -> StringUtils
                    .startsWithIgnoreCase(backupPath.getFileName().toString(),
                        HALO_BACKUP_MARKDOWN_PREFIX))
                .map(backupPath -> buildBackupDto(DATA_EXPORT_MARKDOWN_BASE_URI, backupPath))
                .sorted(Comparator.comparingLong(BackupDTO::getUpdateTime).reversed())
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw new ServiceException("Failed to fetch backups", e);
        }
    }

    @Override
    public void deleteMarkdown(String filename) {
        Assert.hasText(filename, "File name must not be blank");

        Path backupRootPath = Paths.get(haloProperties.getBackupMarkdownDir());

        // Get backup path
        Path backupPath = backupRootPath.resolve(filename);

        // Check directory traversal
        checkDirectoryTraversal(backupRootPath, backupPath);

        try {
            // Delete backup file
            Files.delete(backupPath);
        } catch (NoSuchFileException e) {
            throw new NotFoundException("The file " + filename + " was not found", e);
        } catch (IOException e) {
            throw new ServiceException("Failed to delete backup", e);
        }
    }

    /**
     * Builds backup dto.
     *
     * @param backupPath backup path must not be null
     * @return backup dto
     */
    private BackupDTO buildBackupDto(@NonNull String basePath, @NonNull Path backupPath) {
        Assert.notNull(basePath, "Base path must not be null");
        Assert.notNull(backupPath, "Backup path must not be null");

        String backupFileName = backupPath.getFileName().toString();
        BackupDTO backup = new BackupDTO();
        try {
            backup.setDownloadLink(buildDownloadUrl(basePath, backupFileName));
            backup.setFilename(backupFileName);
            backup.setUpdateTime(Files.getLastModifiedTime(backupPath).toMillis());
            backup.setFileSize(Files.size(backupPath));
        } catch (IOException e) {
            throw new ServiceException("Failed to access file " + backupPath, e);
        }

        return backup;
    }

    /**
     * Builds download url.
     *
     * @param filename filename must not be blank
     * @return download url
     */
    @NonNull
    private String buildDownloadUrl(@NonNull String basePath, @NonNull String filename) {
        Assert.notNull(basePath, "Base path must not be null");
        Assert.hasText(filename, "File name must not be blank");

        // Composite http url
        String backupUri = basePath + HaloUtils.URL_SEPARATOR + filename;

        // Get a one-time token
        String oneTimeToken = oneTimeTokenService.create(backupUri);

        // Build full url
        return HaloUtils.compositeHttpUrl(optionService.getBlogBaseUrl(), backupUri)
            + "?"
            + HaloConst.ONE_TIME_TOKEN_QUERY_NAME
            + "=" + oneTimeToken;
    }

}
