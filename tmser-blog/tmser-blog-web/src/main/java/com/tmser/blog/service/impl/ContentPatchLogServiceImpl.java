package com.tmser.blog.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import com.tmser.blog.exception.NotFoundException;
import com.tmser.blog.model.entity.Content;
import com.tmser.blog.model.entity.Content.ContentDiff;
import com.tmser.blog.model.entity.Content.PatchedContent;
import com.tmser.blog.model.entity.ContentPatchLog;
import com.tmser.blog.model.enums.PostStatus;
import com.tmser.blog.repository.ContentPatchLogRepository;
import com.tmser.blog.repository.ContentRepository;
import com.tmser.blog.service.ContentPatchLogService;
import com.tmser.blog.utils.PatchUtils;

/**
 * Content patch log service.
 *
 * @author guqing
 * @since 2022-01-04
 */
@Service
public class ContentPatchLogServiceImpl implements ContentPatchLogService {

    /**
     * base version of content patch log.
     */
    public static final int BASE_VERSION = 1;

    private final ContentPatchLogRepository contentPatchLogRepository;

    private final ContentRepository contentRepository;

    public ContentPatchLogServiceImpl(ContentPatchLogRepository contentPatchLogRepository,
        ContentRepository contentRepository) {
        this.contentPatchLogRepository = contentPatchLogRepository;
        this.contentRepository = contentRepository;
    }

    /**
     * Gets post content by post id.
     *
     * @param postId post id
     * @return a post content of postId
     */
    protected Optional<Content> getContentByPostId(Serializable postId) {
        return Optional.ofNullable(contentRepository.selectById(postId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ContentPatchLog createOrUpdate(Integer postId, String content, String originalContent) {
        Integer version = getVersionByPostId(postId);
        if (existDraftBy(postId)) {
            return updateDraftBy(postId, content, originalContent);
        }
        return createDraftContent(postId, version, content, originalContent);
    }

    private Integer getVersionByPostId(Integer postId) {
        Integer version;
        ContentPatchLog latestPatchLog =
            contentPatchLogRepository.findFirstByPostIdOrderByVersionDesc(postId);

        if (latestPatchLog == null) {
            // There is no patchLog record
            version = 1;
        } else if (PostStatus.PUBLISHED.equals(latestPatchLog.getStatus())) {
            // There is no draft, a draft record needs to be created
            // so the version number needs to be incremented
            version = latestPatchLog.getVersion() + 1;
        } else {
            // There is a draft record,Only the content needs to be updated
            // so the version number remains unchanged
            version = latestPatchLog.getVersion();
        }
        return version;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(ContentPatchLog contentPatchLog) {
        contentPatchLogRepository.insert(contentPatchLog);
    }

    private ContentPatchLog createDraftContent(Integer postId, Integer version,
        String formatContent, String originalContent) {
        ContentPatchLog contentPatchLog =
            buildPatchLog(postId, version, formatContent, originalContent);

        // Sets the upstream version of the current version.
        Integer sourceId = getContentByPostId(postId)
            .map(Content::getPatchLogId)
            .orElse(0);
        contentPatchLog.setSourceId(sourceId);

        contentPatchLogRepository.insert(contentPatchLog);
        return contentPatchLog;
    }

    private ContentPatchLog buildPatchLog(Integer postId, Integer version, String formatContent,
        String originalContent) {
        ContentPatchLog contentPatchLog = new ContentPatchLog();
        if (Objects.equals(version, BASE_VERSION)) {
            contentPatchLog.setContentDiff(formatContent);
            contentPatchLog.setOriginalContentDiff(originalContent);
        } else {
            ContentDiff contentDiff = generateDiff(postId, formatContent, originalContent);
            contentPatchLog.setContentDiff(contentDiff.getDiff());
            contentPatchLog.setOriginalContentDiff(contentDiff.getOriginalDiff());
        }
        contentPatchLog.setPostId(postId);
        contentPatchLog.setStatus(PostStatus.DRAFT);
        ContentPatchLog latestPatchLog =
            contentPatchLogRepository.findFirstByPostIdOrderByVersionDesc(postId);
        if (latestPatchLog != null) {
            contentPatchLog.setVersion(latestPatchLog.getVersion() + 1);
        } else {
            contentPatchLog.setVersion(BASE_VERSION);
        }

        return contentPatchLog;
    }

    private boolean existDraftBy(Integer postId) {
        ContentPatchLog contentPatchLog = new ContentPatchLog();
        contentPatchLog.setPostId(postId);
        contentPatchLog.setStatus(PostStatus.DRAFT);
        return contentPatchLogRepository.selectOne(new QueryWrapper<>(contentPatchLog)) != null;
    }

    private ContentPatchLog updateDraftBy(Integer postId, String formatContent,
        String originalContent) {
        ContentPatchLog draftPatchLog =
            contentPatchLogRepository.findFirstByPostIdAndStatusOrderByVersionDesc(postId,
                PostStatus.DRAFT);
        // Is the draft version 1
        if (Objects.equals(draftPatchLog.getVersion(), BASE_VERSION)) {
            // If it is V1, modify the content directly.
            draftPatchLog.setContentDiff(formatContent);
            draftPatchLog.setOriginalContentDiff(originalContent);
            contentPatchLogRepository.insert(draftPatchLog);
            return draftPatchLog;
        }
        // Generate content diff.
        ContentDiff contentDiff = generateDiff(postId, formatContent, originalContent);
        draftPatchLog.setContentDiff(contentDiff.getDiff());
        draftPatchLog.setOriginalContentDiff(contentDiff.getOriginalDiff());
        contentPatchLogRepository.insert(draftPatchLog);
        return draftPatchLog;
    }

    @Override
    public PatchedContent applyPatch(ContentPatchLog patchLog) {
        Assert.notNull(patchLog, "The contentRecord must not be null.");
        Assert.notNull(patchLog.getVersion(), "The contentRecord.version must not be null.");
        Assert.notNull(patchLog.getPostId(), "The contentRecord.postId must not be null.");

        PatchedContent patchedContent = new PatchedContent();
        if (patchLog.getVersion() == BASE_VERSION) {
            patchedContent.setContent(patchLog.getContentDiff());
            patchedContent.setOriginalContent(patchLog.getOriginalContentDiff());
            return patchedContent;
        }

        ContentPatchLog baseContentRecord =
            contentPatchLogRepository.findByPostIdAndVersion(patchLog.getPostId(), BASE_VERSION);

        String content = PatchUtils.restoreContent(patchLog.getContentDiff(),
            baseContentRecord.getContentDiff());
        patchedContent.setContent(content);

        String originalContent = PatchUtils.restoreContent(patchLog.getOriginalContentDiff(),
            baseContentRecord.getOriginalContentDiff());
        patchedContent.setOriginalContent(originalContent);
        return patchedContent;
    }

    @Override
    public ContentDiff generateDiff(Integer postId, String formatContent, String originalContent) {
        ContentPatchLog basePatchLog =
            contentPatchLogRepository.findByPostIdAndVersion(postId, BASE_VERSION);

        ContentDiff contentDiff = new ContentDiff();
        String contentChanges =
            PatchUtils.diffToJsonPatch(basePatchLog.getContentDiff(), formatContent);
        contentDiff.setDiff(contentChanges);

        String originalContentChanges =
            PatchUtils.diffToJsonPatch(basePatchLog.getOriginalContentDiff(), originalContent);
        contentDiff.setOriginalDiff(originalContentChanges);
        return contentDiff;
    }

    @Override
    public ContentPatchLog getDraftByPostId(Integer postId) {
        return contentPatchLogRepository.findFirstByPostIdAndStatusOrderByVersionDesc(postId,
            PostStatus.DRAFT);
    }

    @Override
    public PatchedContent getByPostId(Integer postId) {
        ContentPatchLog contentPatchLog =
            contentPatchLogRepository.findFirstByPostIdOrderByVersionDesc(postId);
        if (contentPatchLog == null) {
            throw new NotFoundException(
                "Post content patch log was not found or has been deleted.");
        }
        return applyPatch(contentPatchLog);
    }

    @Override
    public PatchedContent getPatchedContentById(Integer id) {
        ContentPatchLog contentPatchLog = getById(id);
        return applyPatch(contentPatchLog);
    }

    @Override
    public ContentPatchLog getById(Integer id) {
        return Optional.ofNullable(contentPatchLogRepository.selectById(id))
            .orElseThrow(() -> new NotFoundException(
                "Post content patch log was not found or has been deleted."));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ContentPatchLog> removeByPostId(Integer postId) {
        List<ContentPatchLog> patchLogsToDelete = contentPatchLogRepository.findAllByPostId(postId);
        contentPatchLogRepository.deleteBatchIds(patchLogsToDelete.stream().map(ContentPatchLog::getId).collect(Collectors.toList()));
        return patchLogsToDelete;
    }
}
