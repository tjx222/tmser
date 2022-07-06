package com.tmser.blog.service.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import com.tmser.blog.exception.NotFoundException;
import com.tmser.blog.model.entity.Content;
import com.tmser.blog.model.entity.Content.PatchedContent;
import com.tmser.blog.model.entity.ContentPatchLog;
import com.tmser.blog.model.enums.PostStatus;
import com.tmser.blog.repository.ContentRepository;
import com.tmser.blog.service.ContentPatchLogService;
import com.tmser.blog.service.ContentService;
import com.tmser.blog.service.base.AbstractCrudService;

/**
 * Base content service implementation.
 *
 * @author guqing
 * @date 2022-01-07
 */
@Service
public class ContentServiceImpl extends AbstractCrudService<Content, Integer>
    implements ContentService {

    private final ContentRepository contentRepository;

    private final ContentPatchLogService contentPatchLogService;

    protected ContentServiceImpl(ContentRepository contentRepository,
        ContentPatchLogService contentPatchLogService) {
        super(contentRepository);
        this.contentRepository = contentRepository;
        this.contentPatchLogService = contentPatchLogService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOrUpdateDraftBy(Integer postId, String content,
        String originalContent) {
        Assert.notNull(postId, "The postId must not be null.");
        // First, we need to save the contentPatchLog
        ContentPatchLog contentPatchLog =
            contentPatchLogService.createOrUpdate(postId, content, originalContent);

        // then update the value of headPatchLogId field.
        Optional<Content> savedContentOptional = Optional.ofNullable(contentRepository.selectById(postId));
        if (savedContentOptional.isPresent()) {
            Content savedContent = savedContentOptional.get();
            savedContent.setHeadPatchLogId(contentPatchLog.getId());
            contentRepository.updateById(savedContent);
            return;
        }

        // If the content record does not exist, it needs to be created
        Content postContent = new Content();
        postContent.setPatchLogId(contentPatchLog.getId());
        postContent.setContent(content);
        postContent.setOriginalContent(originalContent);
        postContent.setId(postId);
        postContent.setStatus(PostStatus.DRAFT);
        postContent.setHeadPatchLogId(contentPatchLog.getId());
        contentRepository.insert(postContent);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Content publishContent(Integer postId) {
        ContentPatchLog contentPatchLog = contentPatchLogService.getDraftByPostId(postId);
        if (contentPatchLog == null) {
            return contentRepository.selectById(postId);
        }
        contentPatchLog.setStatus(PostStatus.PUBLISHED);
        contentPatchLog.setPublishTime(new Date());
        contentPatchLogService.save(contentPatchLog);

        Content postContent = getById(postId);
        postContent.setPatchLogId(contentPatchLog.getId());
        postContent.setStatus(PostStatus.PUBLISHED);

        PatchedContent patchedContent = contentPatchLogService.applyPatch(contentPatchLog);
        postContent.setContent(patchedContent.getContent());
        postContent.setOriginalContent(patchedContent.getOriginalContent());

        contentRepository.updateById(postContent);

        return postContent;
    }

    @Override
    @NonNull
    public Content getById(@NonNull Integer postId) {
        Assert.notNull(postId, "The postId must not be null.");
        return Optional.ofNullable(contentRepository.selectById(postId))
            .orElseThrow(() -> new NotFoundException("content was not found or has been deleted"));
    }

    @Override
    public Boolean draftingInProgress(Integer postId) {
        ContentPatchLog draft = contentPatchLogService.getDraftByPostId(postId);
        return Objects.nonNull(draft);
    }
}
