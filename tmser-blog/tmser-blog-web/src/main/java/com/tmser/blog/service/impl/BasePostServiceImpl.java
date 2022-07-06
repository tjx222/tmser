package com.tmser.blog.service.impl;


import com.tmser.blog.exception.AlreadyExistsException;
import com.tmser.blog.exception.BadRequestException;
import com.tmser.blog.exception.NotFoundException;
import com.tmser.blog.exception.ServiceException;
import com.tmser.blog.model.dto.post.BasePostDetailDTO;
import com.tmser.blog.model.dto.post.BasePostMinimalDTO;
import com.tmser.blog.model.dto.post.BasePostSimpleDTO;
import com.tmser.blog.model.entity.BasePost;
import com.tmser.blog.model.entity.Content;
import com.tmser.blog.model.entity.Content.PatchedContent;
import com.tmser.blog.model.enums.PostStatus;
import com.tmser.blog.model.properties.PostProperties;
import com.tmser.blog.repository.base.BasePostRepository;
import com.tmser.blog.service.ContentPatchLogService;
import com.tmser.blog.service.ContentService;
import com.tmser.blog.service.OptionService;
import com.tmser.blog.service.base.AbstractCrudService;
import com.tmser.blog.service.base.BasePostService;
import com.tmser.blog.utils.DateUtils;
import com.tmser.blog.utils.HaloUtils;
import com.tmser.blog.utils.ServiceUtils;
import com.tmser.database.mybatis.MybatisPageHelper;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import com.tmser.model.sort.Sort;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.tmser.model.sort.Sort.Direction.DESC;

/**
 * Base post service implementation.
 *
 * @author johnniang
 * @author ryanwang
 * @author guqing
 * @date 2019-04-24
 */
@Slf4j
public abstract class BasePostServiceImpl<POST extends BasePost>
        extends AbstractCrudService<POST, Integer> implements BasePostService<POST> {

    private final BasePostRepository<POST> basePostRepository;

    private final OptionService optionService;

    private final ContentService contentService;

    private final ContentPatchLogService contentPatchLogService;

    private static final Pattern summaryPattern = Pattern.compile("\t|\r|\n");

    private static final Pattern BLANK_PATTERN = Pattern.compile("\\s");

    public BasePostServiceImpl(BasePostRepository<POST> basePostRepository,
                               OptionService optionService,
                               ContentService contentService,
                               ContentPatchLogService contentPatchLogService) {
        super(basePostRepository);
        this.basePostRepository = basePostRepository;
        this.optionService = optionService;
        this.contentService = contentService;
        this.contentPatchLogService = contentPatchLogService;
    }

    @Override
    public long countVisit() {
        return Optional.ofNullable(basePostRepository.countVisit()).orElse(0L);
    }

    @Override
    public long countLike() {
        return Optional.ofNullable(basePostRepository.countLike()).orElse(0L);
    }

    @Override
    public long countByStatus(PostStatus status) {
        Assert.notNull(status, "Post status must not be null");

        return basePostRepository.countByStatus(status);
    }

    @Override
    public POST getBySlug(String slug) {
        Assert.hasText(slug, "Slug must not be blank");

        return basePostRepository.getBySlug(slug)
                .orElseThrow(() -> new NotFoundException("查询不到该文章的信息").setErrorData(slug));
    }

    @Override
    public POST getBy(PostStatus status, String slug) {
        Assert.notNull(status, "Post status must not be null");
        Assert.hasText(slug, "Post slug must not be blank");

        Optional<POST> postOptional = basePostRepository.getBySlugAndStatus(slug, status);

        return postOptional
                .orElseThrow(() -> new NotFoundException("查询不到该文章的信息").setErrorData(slug));
    }

    @Override
    public POST getBy(PostStatus status, Integer id) {
        Assert.notNull(status, "Post status must not be null");
        Assert.notNull(id, "Post id must not be null");

        Optional<POST> postOptional = basePostRepository.getByIdAndStatus(id, status);

        return postOptional.orElseThrow(() -> new NotFoundException("查询不到该文章的信息").setErrorData(id));
    }

    @Override
    public PatchedContent getLatestContentById(Integer id) {
        return contentPatchLogService.getByPostId(id);
    }

    @Override
    public List<POST> listAllBy(PostStatus status) {
        Assert.notNull(status, "Post status must not be null");

        return basePostRepository.findAllByStatus(status);
    }


    @Override
    public List<POST> listPrevPosts(POST post, int size) {
        Assert.notNull(post, "Post must not be null");

        String indexSort =
                optionService.getByPropertyOfNonNull(PostProperties.INDEX_SORT).toString();

        Page<POST> pageRequest = PageImpl.of(0, size, Sort.by(Sort.Direction.ASC, indexSort));

        switch (indexSort) {
            case "create_time":
                return MybatisPageHelper.fillPageData(basePostRepository
                        .findAllByStatusAndCreateTimeAfter(PostStatus.PUBLISHED, post.getCreateTime(),
                                MybatisPageHelper.changeToMybatisPage(pageRequest)), pageRequest).getContent();
            case "edit_time":
                return MybatisPageHelper.fillPageData(basePostRepository
                        .findAllByStatusAndEditTimeAfter(PostStatus.PUBLISHED, post.getEditTime(),
                                MybatisPageHelper.changeToMybatisPage(pageRequest)), pageRequest).getContent();
            case "visits":
                return MybatisPageHelper.fillPageData(basePostRepository
                        .findAllByStatusAndVisitsAfter(PostStatus.PUBLISHED, post.getVisits(),
                                MybatisPageHelper.changeToMybatisPage(pageRequest)), pageRequest).getContent();
            default:
                return Collections.emptyList();
        }
    }

    @Override
    public List<POST> listNextPosts(POST post, int size) {
        Assert.notNull(post, "Post must not be null");

        String indexSort =
                optionService.getByPropertyOfNonNull(PostProperties.INDEX_SORT).toString();

        Page<POST> pageRequest = PageImpl.of(0, size, Sort.by(DESC, indexSort));

        switch (indexSort) {
            case "create_time":
                return MybatisPageHelper.fillPageData(basePostRepository
                        .findAllByStatusAndCreateTimeBefore(PostStatus.PUBLISHED, post.getCreateTime(),
                                MybatisPageHelper.changeToMybatisPage(pageRequest)), pageRequest).getContent();
            case "edit_time":
                return MybatisPageHelper.fillPageData(basePostRepository
                        .findAllByStatusAndEditTimeBefore(PostStatus.PUBLISHED, post.getEditTime(),
                                MybatisPageHelper.changeToMybatisPage(pageRequest)), pageRequest).getContent();
            case "visits":
                return MybatisPageHelper.fillPageData(basePostRepository
                        .findAllByStatusAndVisitsBefore(PostStatus.PUBLISHED, post.getVisits(),
                                MybatisPageHelper.changeToMybatisPage(pageRequest)), pageRequest).getContent();
            default:
                return Collections.emptyList();
        }
    }

    @Override
    public Optional<POST> getPrevPost(POST post) {
        List<POST> posts = listPrevPosts(post, 1);

        return CollectionUtils.isEmpty(posts) ? Optional.empty() : Optional.of(posts.get(0));
    }

    @Override
    public Optional<POST> getNextPost(POST post) {
        List<POST> posts = listNextPosts(post, 1);

        return CollectionUtils.isEmpty(posts) ? Optional.empty() : Optional.of(posts.get(0));
    }

    @Override
    public Page<POST> pageLatest(int top) {
        Assert.isTrue(top > 0, "Top number must not be less than 0");

        Page latestPageable = PageImpl.of(0, top, Sort.by(DESC, "create_time"));

        return listAll(latestPageable);
    }

    /**
     * Lists latest posts.
     *
     * @param top top number must not be less than 0
     * @return latest posts
     */
    @Override
    public List<POST> listLatest(int top) {
        Assert.isTrue(top > 0, "Top number must not be less than 0");

        Page<POST> latestPageable = PageImpl.of(0, top, Sort.by(DESC, "create_time"));
        return MybatisPageHelper.fillPageData(
                        basePostRepository.findPageByStatus(PostStatus.PUBLISHED,
                                MybatisPageHelper.changeToMybatisPage(latestPageable)), latestPageable)
                .getContent();
    }

    @Override
    public Page<POST> pageBy(Page pageable) {
        Assert.notNull(pageable, "Page info must not be null");

        return listAll(pageable);
    }


    @Override
    public Page<POST> pageBy(PostStatus status, Page pageable) {
        Assert.notNull(status, "Post status must not be null");
        Assert.notNull(pageable, "Page info must not be null");

        return MybatisPageHelper.fillPageData(
                basePostRepository.findPageByStatus(status, MybatisPageHelper.changeToMybatisPage(pageable)), pageable);
    }

    @Override
    @Transactional
    public void increaseVisit(long visits, Integer postId) {
        Assert.isTrue(visits > 0, "Visits to increase must not be less than 1");
        Assert.notNull(postId, "Post id must not be null");

        boolean finishedIncrease;
        if (basePostRepository.getByIdAndStatus(postId, PostStatus.DRAFT).isPresent()) {
            finishedIncrease = true;
            log.info("Post with id: [{}] is a draft and visits will not be updated", postId);
        } else {
            finishedIncrease = basePostRepository.updateVisit(visits, postId) == 1;
        }

        if (!finishedIncrease) {
            log.error("Post with id: [{}] may not be found", postId);
            throw new BadRequestException(
                    "Failed to increase visits " + visits + " for post with id " + postId);
        }
    }

    @Override
    @Transactional
    public void increaseVisit(Integer postId) {
        increaseVisit(1L, postId);
    }

    @Override
    @Transactional
    public void increaseLike(long likes, Integer postId) {
        Assert.isTrue(likes > 0, "Likes to increase must not be less than 1");
        Assert.notNull(postId, "Post id must not be null");

        long affectedRows = basePostRepository.updateLikes(likes, postId);

        if (affectedRows != 1) {
            log.error("Post with id: [{}] may not be found", postId);
            throw new BadRequestException(
                    "Failed to increase likes " + likes + " for post with id " + postId);
        }
    }

    @Override
    @Transactional
    public void increaseLike(Integer postId) {
        increaseLike(1L, postId);
    }

    /**
     * @param post post for article
     * @return post with handled data
     */
    @Override
    @Transactional
    public POST createOrUpdateBy(POST post) {
        Assert.notNull(post, "Post must not be null");
        PatchedContent postContent = post.getContent();
        // word count stat
        post.setWordCount(htmlFormatWordCount(postContent.getContent()));
        post.setContent(postContent);

        POST savedPost;
        // Create or update post
        if (ServiceUtils.isEmptyId(post.getId())) {
            // The sheet will be created
            savedPost = create(post);
            contentService.createOrUpdateDraftBy(post.getId(),
                    postContent.getContent(), postContent.getOriginalContent());
        } else {
            // The sheet will be updated
            // Set edit time
            post.setEditTime(DateUtils.now());
            contentService.createOrUpdateDraftBy(post.getId(),
                    postContent.getContent(), postContent.getOriginalContent());
            // Update it
            savedPost = update(post);
        }

        if (PostStatus.PUBLISHED.equals(post.getStatus())
                || PostStatus.INTIMATE.equals(post.getStatus())) {
            contentService.publishContent(post.getId());
        }
        return savedPost;
    }

    @Override
    public POST filterIfEncrypt(POST post) {
        Assert.notNull(post, "Post must not be null");

        if (StringUtils.isNotBlank(post.getPassword())) {
            String tip = "The post is encrypted by author";
            post.setSummary(tip);

            Content postContent = new Content();
            postContent.setContent(tip);
            postContent.setOriginalContent(tip);
            post.setContent(PatchedContent.of(postContent));
        }

        return post;
    }

    @Override
    public BasePostMinimalDTO convertToMinimal(POST post) {
        Assert.notNull(post, "Post must not be null");

        return new BasePostMinimalDTO().convertFrom(post);
    }

    @Override
    public List<BasePostMinimalDTO> convertToMinimal(List<POST> posts) {
        if (CollectionUtils.isEmpty(posts)) {
            return Collections.emptyList();
        }

        return posts.stream()
                .map(this::convertToMinimal)
                .collect(Collectors.toList());
    }

    @Override
    public Page<BasePostMinimalDTO> convertToMinimal(Page<POST> postPage) {
        Assert.notNull(postPage, "Post page must not be null");
        Page<BasePostMinimalDTO> pageResult = PageImpl.of(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        return pageResult.setContent(postPage.getContent().stream().map(pst -> convertToMinimal(pst)).collect(Collectors.toList()));
    }

    @Override
    public BasePostSimpleDTO convertToSimple(POST post) {
        Assert.notNull(post, "Post must not be null");

        BasePostSimpleDTO basePostSimpleDTO = new BasePostSimpleDTO().convertFrom(post);

        // Set summary
        generateAndSetSummaryIfAbsent(post, basePostSimpleDTO);

        // Post currently drafting in process
        Boolean isInProcess = contentService.draftingInProgress(post.getId());
        basePostSimpleDTO.setInProgress(isInProcess);

        return basePostSimpleDTO;
    }

    @Override
    public List<BasePostSimpleDTO> convertToSimple(List<POST> posts) {
        if (CollectionUtils.isEmpty(posts)) {
            return Collections.emptyList();
        }

        return posts.stream()
                .map(this::convertToSimple)
                .collect(Collectors.toList());
    }

    @Override
    public Page<BasePostSimpleDTO> convertToSimple(Page<POST> postPage) {
        Assert.notNull(postPage, "Post page must not be null");
        Page<BasePostSimpleDTO> pageResult = PageImpl.of(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        return pageResult.setContent(postPage.getContent().stream().map(pst -> convertToSimple(pst)).collect(Collectors.toList()));
    }

    @Override
    public BasePostDetailDTO convertToDetail(POST post) {
        Assert.notNull(post, "Post must not be null");

        BasePostDetailDTO postDetail = new BasePostDetailDTO().convertFrom(post);

        // Post currently drafting in process
        Boolean isInProcess = contentService.draftingInProgress(post.getId());
        postDetail.setInProgress(isInProcess);

        return postDetail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public POST updateDraftContent(String content, String originalContent, Integer postId) {
        Assert.isTrue(!ServiceUtils.isEmptyId(postId), "Post id must not be empty");

        if (originalContent == null) {
            originalContent = "";
        }

        contentService.createOrUpdateDraftBy(postId, content, originalContent);

        POST post = getById(postId);
        post.setContent(getLatestContentById(postId));

        return post;
    }

    @Override
    @Transactional
    public POST updateStatus(PostStatus status, Integer postId) {
        Assert.notNull(status, "Post status must not be null");
        Assert.isTrue(!ServiceUtils.isEmptyId(postId), "Post id must not be empty");

        // Get post
        POST post = getById(postId);

        if (!status.equals(post.getStatus())) {
            // Update post
            int updatedRows = basePostRepository.updateStatus(status, postId);
            if (updatedRows != 1) {
                throw new ServiceException(
                        "Failed to update post status of post with id " + postId);
            }

            post.setStatus(status);
        }

        // Sync content
        if (PostStatus.PUBLISHED.equals(status)) {
            // If publish this post, then convert the formatted content
            Content postContent = contentService.publishContent(postId);
            post.setContent(PatchedContent.of(postContent));
        }

        return post;
    }

    @Override
    @Transactional
    public List<POST> updateStatusByIds(List<Integer> ids, PostStatus status) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return ids.stream().map(id -> {
            return updateStatus(status, id);
        }).collect(Collectors.toList());
    }

    @Override
    public String generateDescription(@Nullable String content) {
        if (StringUtils.isBlank(content)) {
            return StringUtils.EMPTY;
        }

        String text = HaloUtils.cleanHtmlTag(content);

        Matcher matcher = summaryPattern.matcher(text);
        text = matcher.replaceAll("");

        // Get summary length
        Integer summaryLength =
                optionService.getByPropertyOrDefault(PostProperties.SUMMARY_LENGTH, Integer.class, 150);

        return StringUtils.substring(text, 0, summaryLength);
    }

    @Override
    public POST create(POST post) {
        // Check title
        slugMustNotExist(post);

        return super.create(post);
    }

    @Override
    public POST update(POST post) {
        // Check title
        slugMustNotExist(post);

        return super.update(post);
    }

    @Override
    public Content getContentById(Integer postId) {
        Assert.notNull(postId, "The postId must not be null.");
        return contentService.getById(postId);
    }

    /**
     * Check if the slug is exist.
     *
     * @param post post must not be null
     */
    protected void slugMustNotExist(@NonNull POST post) {
        Assert.notNull(post, "Post must not be null");

        // Get slug count
        boolean exist;

        if (ServiceUtils.isEmptyId(post.getId())) {
            // The sheet will be created
            exist = basePostRepository.existsBySlug(post.getSlug());
        } else {
            // The sheet will be updated
            exist = basePostRepository.existsByIdNotAndSlug(post.getId(), post.getSlug());
        }

        if (exist) {
            throw new AlreadyExistsException("文章别名 " + post.getSlug() + " 已存在");
        }
    }

    @NonNull
    protected String generateSummary(@Nullable String htmlContent) {
        if (StringUtils.isBlank(htmlContent)) {
            return StringUtils.EMPTY;
        }

        String text = HaloUtils.cleanHtmlTag(htmlContent);

        Matcher matcher = summaryPattern.matcher(text);
        text = matcher.replaceAll("");

        // Get summary length
        Integer summaryLength =
                optionService.getByPropertyOrDefault(PostProperties.SUMMARY_LENGTH, Integer.class, 150);

        return StringUtils.substring(text, 0, summaryLength);
    }

    protected <T extends BasePostSimpleDTO> void generateAndSetSummaryIfAbsent(POST post,
                                                                               T postVo) {
        Assert.notNull(post, "The post must not be null.");
        if (StringUtils.isNotBlank(postVo.getSummary())) {
            return;
        }

        PatchedContent patchedContent = post.getContentOfNullable();
        if (patchedContent == null) {
            Content postContent = getContentById(post.getId());
            postVo.setSummary(generateSummary(postContent.getContent()));
        } else {
            postVo.setSummary(generateSummary(patchedContent.getContent()));
        }
    }

    // CS304 issue link : https://github.com/halo-dev/halo/issues/1224

    /**
     * @param htmlContent the markdown style content
     * @return word count except space and line separator
     */

    public static long htmlFormatWordCount(String htmlContent) {
        if (htmlContent == null) {
            return 0;
        }

        String cleanContent = HaloUtils.cleanHtmlTag(htmlContent);

        Matcher matcher = BLANK_PATTERN.matcher(cleanContent);

        int count = 0;

        while (matcher.find()) {
            count++;
        }

        return cleanContent.length() - count;
    }
}
