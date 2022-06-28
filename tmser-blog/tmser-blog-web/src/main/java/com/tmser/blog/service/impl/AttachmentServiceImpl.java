package com.tmser.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tmser.blog.exception.AlreadyExistsException;
import com.tmser.blog.handler.file.FileHandlers;
import com.tmser.blog.model.dto.AttachmentDTO;
import com.tmser.blog.model.entity.Attachment;
import com.tmser.blog.model.enums.AttachmentType;
import com.tmser.blog.model.params.AttachmentQuery;
import com.tmser.blog.model.properties.AttachmentProperties;
import com.tmser.blog.model.support.UploadResult;
import com.tmser.blog.repository.AttachmentRepository;
import com.tmser.blog.service.AttachmentService;
import com.tmser.blog.service.OptionService;
import com.tmser.blog.service.base.AbstractCrudService;
import com.tmser.blog.utils.HaloUtils;
import com.tmser.database.mybatis.MybatisPageHelper;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * AttachmentService implementation
 *
 * @author ryanwang
 * @author johnniang
 * @date 2019-03-14
 */
@Slf4j
@Service
public class AttachmentServiceImpl extends AbstractCrudService<Attachment, Integer>
        implements AttachmentService {

    private final AttachmentRepository attachmentRepository;

    private final OptionService optionService;

    private final FileHandlers fileHandlers;

    public AttachmentServiceImpl(AttachmentRepository attachmentRepository,
                                 OptionService optionService,
                                 FileHandlers fileHandlers) {
        super(attachmentRepository);
        this.attachmentRepository = attachmentRepository;
        this.optionService = optionService;
        this.fileHandlers = fileHandlers;
    }

    @Override
    public Page<AttachmentDTO> pageDtosBy(@NonNull Page page,
                                          AttachmentQuery attachmentQuery) {
        Assert.notNull(page, "Page info must not be null");
        Attachment model = new Attachment();
        model.setMediaType(attachmentQuery.getMediaType());
        model.setType(attachmentQuery.getAttachmentType());
        QueryWrapper<Attachment> wrapper = new QueryWrapper<>(model);
        if (StringUtils.isNotBlank(attachmentQuery.getKeyword())) {
            wrapper.or().like("name", attachmentQuery.getKeyword());
        }
        // List all
        Page<Attachment> attachmentPage = MybatisPageHelper.fillPageData(
                attachmentRepository.selectPage(MybatisPageHelper.changeToMybatisPage(page), wrapper), page);
        Page<AttachmentDTO> resultPage = PageImpl.of(page.getCurrent(), page.getSize(), page.getTotal());
        // Convert and return
        return resultPage.setContent(attachmentPage.getContent()
                .stream()
                .map(att -> convertToDto(att)).collect(Collectors.toList()));
    }

    @Override
    public Attachment upload(MultipartFile file) {
        Assert.notNull(file, "Multipart file must not be null");

        AttachmentType attachmentType = getAttachmentType();

        log.debug("Starting uploading... type: [{}], file: [{}]", attachmentType,
                file.getOriginalFilename());

        // Upload file
        UploadResult uploadResult = fileHandlers.upload(file, attachmentType);

        log.debug("Attachment type: [{}]", attachmentType);
        log.debug("Upload result: [{}]", uploadResult);

        // Build attachment
        Attachment attachment = new Attachment();
        attachment.setName(uploadResult.getFilename());
        // Convert separator
        attachment.setPath(HaloUtils.changeFileSeparatorToUrlSeparator(uploadResult.getFilePath()));
        attachment.setFileKey(uploadResult.getKey());
        attachment.setThumbPath(uploadResult.getThumbPath());
        attachment.setMediaType(uploadResult.getMediaType().toString());
        attachment.setSuffix(uploadResult.getSuffix());
        attachment.setWidth(uploadResult.getWidth());
        attachment.setHeight(uploadResult.getHeight());
        attachment.setSize(uploadResult.getSize());
        attachment.setType(attachmentType);

        log.debug("Creating attachment: [{}]", attachment);

        // Create and return
        return create(attachment);
    }

    @Override
    public Attachment removePermanently(Integer id) {
        // Remove it from database
        Attachment deletedAttachment = removeById(id);

        // Remove the file
        fileHandlers.delete(deletedAttachment);

        log.debug("Deleted attachment: [{}]", deletedAttachment);

        return deletedAttachment;
    }

    @Override
    public List<Attachment> removePermanently(@Nullable Collection<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }

        return ids.stream().map(this::removePermanently).collect(Collectors.toList());
    }

    @Override
    public AttachmentDTO convertToDto(Attachment attachment) {
        Assert.notNull(attachment, "Attachment must not be null");

        // Get blog base url
        String blogBaseUrl = optionService.getBlogBaseUrl();

        Boolean enabledAbsolutePath = optionService.isEnabledAbsolutePath();

        // Convert to output dto
        AttachmentDTO attachmentDTO = new AttachmentDTO().convertFrom(attachment);

        if (Objects.equals(attachmentDTO.getType(), AttachmentType.LOCAL)) {
            // Append blog base url to path and thumbnail
            String fullPath = StringUtils
                    .join(enabledAbsolutePath ? blogBaseUrl : "", "/", attachmentDTO.getPath());
            String fullThumbPath = StringUtils
                    .join(enabledAbsolutePath ? blogBaseUrl : "", "/", attachmentDTO.getThumbPath());

            // Set full path and full thumb path
            attachmentDTO.setPath(fullPath);
            attachmentDTO.setThumbPath(fullThumbPath);
        }

        return attachmentDTO;
    }

    @Override
    public List<String> listAllMediaType() {
        return attachmentRepository.findAllMediaType();
    }

    @Override
    public List<AttachmentType> listAllType() {
        return attachmentRepository.findAllType();
    }


    @Override
    public Attachment create(Attachment attachment) {
        Assert.notNull(attachment, "Attachment must not be null");

        // Check attachment path
        pathMustNotExist(attachment);

        return super.create(attachment);
    }


    /**
     * Attachment path must not be exist.
     *
     * @param attachment attachment must not be null
     */
    private void pathMustNotExist(@NonNull Attachment attachment) {
        Assert.notNull(attachment, "Attachment must not be null");

        long pathCount = attachmentRepository.countByPath(attachment.getPath());

        if (pathCount > 0) {
            throw new AlreadyExistsException("附件路径为 " + attachment.getPath() + " 已经存在");
        }
    }

    /**
     * Get attachment type from options.
     *
     * @return attachment type
     */
    @NonNull
    private AttachmentType getAttachmentType() {
        return Objects.requireNonNull(optionService
                .getEnumByPropertyOrDefault(AttachmentProperties.ATTACHMENT_TYPE, AttachmentType.class,
                        AttachmentType.LOCAL));
    }
}
