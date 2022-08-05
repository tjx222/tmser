package com.tmser.blog.handler.file;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.tmser.blog.model.properties.BlogProperties;
import com.tmser.blog.model.properties.TencentCosProperties;
import com.tmser.blog.service.OptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import com.tmser.blog.exception.FileOperationException;
import com.tmser.blog.exception.RepeatTypeException;
import com.tmser.blog.model.entity.Attachment;
import com.tmser.blog.model.enums.AttachmentType;
import com.tmser.blog.model.support.UploadResult;

import javax.annotation.Resource;

/**
 * File handler manager.
 *
 * @author johnniang
 * @date 2019-03-27
 */
@Slf4j
@Component
public class FileHandlers {

    @Resource
    private OptionService optionService;
    /**
     * File handler container.
     */
    private final ConcurrentHashMap<AttachmentType, FileHandler> fileHandlers =
            new ConcurrentHashMap<>(16);

    public FileHandlers(ApplicationContext applicationContext) {
        // Add all file handler
        addFileHandlers(applicationContext.getBeansOfType(FileHandler.class).values());
        log.info("Registered {} file handler(s)", fileHandlers.size());
    }

    /**
     * Uploads files.
     *
     * @param file           multipart file must not be null
     * @param attachmentType attachment type must not be null
     * @return upload result
     * @throws FileOperationException throws when fail to delete attachment or no available file
     *                                handler to upload it
     */
    @NonNull
    public UploadResult upload(@NonNull MultipartFile file,
                               @NonNull AttachmentType attachmentType) {
        return getSupportedType(attachmentType).upload(file);
    }

    /**
     * Deletes attachment.
     *
     * @param attachment attachment detail must not be null
     * @throws FileOperationException throws when fail to delete attachment or no available file
     *                                handler to delete it
     */
    public void delete(@NonNull Attachment attachment) {
        Assert.notNull(attachment, "Attachment must not be null");
        getSupportedType(attachment.getType())
                .delete(attachment.getFileKey());
    }

    public String preview(@NonNull Attachment attachment) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("ci-process", "doc-preview");
        params.put("dstType", "html");
        params.put("copyable", "0");
        String region =
                optionService.getByPropertyOfNonNull(BlogProperties.BLOG_TITLE).toString();
        params.put("htmlwaterword", "");
        return getSupportedType(attachment.getType()).generateUrl(attachment.getFileKey(), params);
    }

    /**
     * Adds file handlers.
     *
     * @param fileHandlers file handler collection
     * @return current file handlers
     */
    @NonNull
    public FileHandlers addFileHandlers(@Nullable Collection<FileHandler> fileHandlers) {
        if (!CollectionUtils.isEmpty(fileHandlers)) {
            for (FileHandler handler : fileHandlers) {
                if (this.fileHandlers.containsKey(handler.getAttachmentType())) {
                    throw new RepeatTypeException("Same attachment type implements must be unique");
                }
                this.fileHandlers.put(handler.getAttachmentType(), handler);
            }
        }
        return this;
    }

    private FileHandler getSupportedType(AttachmentType type) {
        FileHandler handler =
                fileHandlers.getOrDefault(type, fileHandlers.get(AttachmentType.LOCAL));
        if (handler == null) {
            throw new FileOperationException("No available file handlers to operate the file")
                    .setErrorData(type);
        }
        return handler;
    }
}
