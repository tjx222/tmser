package com.tmser.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tmser.blog.event.comment.CommentNewEvent;
import com.tmser.blog.event.comment.CommentReplyEvent;
import com.tmser.blog.exception.BadRequestException;
import com.tmser.blog.exception.NotFoundException;
import com.tmser.blog.model.dto.BaseCommentDTO;
import com.tmser.blog.model.entity.BaseComment;
import com.tmser.blog.model.entity.User;
import com.tmser.blog.model.enums.CommentStatus;
import com.tmser.blog.model.params.BaseCommentParam;
import com.tmser.blog.model.params.CommentQuery;
import com.tmser.blog.model.projection.CommentChildrenCountProjection;
import com.tmser.blog.model.projection.CommentCountProjection;
import com.tmser.blog.model.properties.BlogProperties;
import com.tmser.blog.model.properties.CommentProperties;
import com.tmser.blog.model.support.CommentPage;
import com.tmser.blog.model.vo.BaseCommentVO;
import com.tmser.blog.model.vo.BaseCommentWithParentVO;
import com.tmser.blog.model.vo.CommentWithHasChildrenVO;
import com.tmser.blog.repository.base.BaseCommentRepository;
import com.tmser.blog.security.authentication.Authentication;
import com.tmser.blog.security.context.SecurityContextHolder;
import com.tmser.blog.service.OptionService;
import com.tmser.blog.service.UserService;
import com.tmser.blog.service.base.AbstractCrudService;
import com.tmser.blog.service.base.BaseCommentService;
import com.tmser.blog.utils.HaloUtils;
import com.tmser.blog.utils.ServiceUtils;
import com.tmser.blog.utils.ServletUtils;
import com.tmser.blog.utils.ValidationUtils;
import com.tmser.database.mybatis.MybatisPageHelper;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import com.tmser.model.page.Pageable;
import com.tmser.model.sort.Sort;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.tmser.model.sort.Sort.Direction.ASC;
import static com.tmser.model.sort.Sort.Direction.DESC;

/**
 * Base comment service implementation.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-04-24
 */
@Slf4j
public abstract class BaseCommentServiceImpl<COMMENT extends BaseComment>
        extends AbstractCrudService<COMMENT, Long> implements BaseCommentService<COMMENT> {

    protected final OptionService optionService;
    protected final UserService userService;
    protected final ApplicationEventPublisher eventPublisher;
    private final BaseCommentRepository<COMMENT> baseCommentRepository;

    public BaseCommentServiceImpl(BaseCommentRepository<COMMENT> baseCommentRepository,
                                  OptionService optionService,
                                  UserService userService, ApplicationEventPublisher eventPublisher) {
        super(baseCommentRepository);
        this.baseCommentRepository = baseCommentRepository;
        this.optionService = optionService;
        this.userService = userService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @NonNull
    public List<COMMENT> listBy(@NonNull Integer postId) {
        Assert.notNull(postId, "Post id must not be null");

        return baseCommentRepository.findAllByPostId(postId);
    }

    @Override
    @NonNull
    public Page<COMMENT> pageLatest(@NonNull int top) {
        return pageLatest(top, null);
    }

    @Override
    @NonNull
    public Page<COMMENT> pageLatest(int top, CommentStatus status) {
        Pageable pageable = ServiceUtils.buildLatestPageable(top);
        if (status == null) {
            return listAll(pageable);
        }

        return MybatisPageHelper.fillPageData(
                baseCommentRepository.findAllByStatus(status, MybatisPageHelper.changeToMybatisPage(pageable)), pageable);
    }


    @Override
    @NonNull
    public Page<BaseCommentVO> pageVosAllBy(@NonNull Integer postId, @NonNull Page page) {
        Assert.notNull(postId, "Post id must not be null");
        Assert.notNull(page, "Page info must not be null");

        log.debug("Getting comment tree view of post: [{}], Page info: [{}]", postId, page);

        // List all the top comments (Caution: This list will be cleared)
        List<COMMENT> comments = baseCommentRepository.findAllByPostId(postId);

        return pageVosBy(comments, page, null);
    }

    @Override
    @NonNull
    public Page<BaseCommentVO> pageVosBy(@NonNull List<COMMENT> comments, @NonNull Page page, Sort sort) {
        Assert.notNull(comments, "Comments must not be null");
        Assert.notNull(page, "Page info must not be null");

        Comparator<BaseCommentVO> commentComparator =
                buildCommentComparator(sort != null ? sort : Sort.by(DESC, "create_time"));

        // Convert to vo
        List<BaseCommentVO> topComments = convertToVo(comments, commentComparator);

        List<BaseCommentVO> pageContent;

        // Calc the shear index
        int startIndex = Long.valueOf(page.getCurrent() * page.getSize()).intValue();
        if (startIndex >= topComments.size() || startIndex < 0) {
            pageContent = Collections.emptyList();
        } else {
            int endIndex = startIndex + Long.valueOf(page.getPages()).intValue();
            if (endIndex > topComments.size()) {
                endIndex = topComments.size();
            }

            log.debug("Top comments size: [{}]", topComments.size());
            log.debug("Start index: [{}]", startIndex);
            log.debug("End index: [{}]", endIndex);

            pageContent = topComments.subList(startIndex, endIndex);
        }

        return new CommentPage(pageContent, page, topComments.size(), comments.size());
    }

    @Override
    @NonNull
    public Page<BaseCommentVO> pageVosBy(@NonNull Integer postId, @NonNull Page page) {
        Assert.notNull(postId, "Post id must not be null");
        Assert.notNull(page, "Page info must not be null");

        log.debug("Getting comment tree view of post: [{}], Page info: [{}]", postId, page);

        // List all the top comments (Caution: This list will be cleared)
        List<COMMENT> comments =
                baseCommentRepository.findAllByPostIdAndStatus(postId, CommentStatus.PUBLISHED);

        return pageVosBy(comments, page, null);
    }

    @Override
    public Page<BaseCommentVO> pageVosBy(@NonNull Integer postId, @NonNull Page pageable, Sort sort) {
        Assert.notNull(postId, "Post id must not be null");
        Assert.notNull(pageable, "Page info must not be null");

        log.debug("Getting comment tree view of post: [{}], Page info: [{}]", postId, pageable);

        // List all the top comments (Caution: This list will be cleared)
        List<COMMENT> comments =
                baseCommentRepository.findAllByPostIdAndStatus(postId, CommentStatus.PUBLISHED);

        return pageVosBy(comments, pageable, sort);
    }

    @Override
    @NonNull
    public Page<BaseCommentWithParentVO> pageWithParentVoBy(@NonNull Integer postId,
                                                            @NonNull Page page) {
        Assert.notNull(postId, "Post id must not be null");

        log.debug("Getting comment list view of post: [{}], Page info: [{}]", postId, page);

        // List all the top comments (Caution: This list will be cleared)
        Page<COMMENT> commentPage = MybatisPageHelper.fillPageData(baseCommentRepository
                .findPageByPostIdAndStatus(postId, CommentStatus.PUBLISHED, MybatisPageHelper.changeToMybatisPage(page)), page);

        // Get all comments
        List<COMMENT> comments = commentPage.getContent();

        // Get all comment parent ids
        Set<Long> parentIds = ServiceUtils.fetchProperty(comments, COMMENT::getParentId);

        // Get all parent comments
        List<COMMENT> parentComments =
                baseCommentRepository.findAllByIdIn(parentIds, page.getSort());

        // Convert to comment map (Key: comment id, value: comment)
        Map<Long, COMMENT> parentCommentMap =
                ServiceUtils.convertToMap(parentComments, COMMENT::getId);

        Map<Long, BaseCommentWithParentVO> parentCommentVoMap =
                new HashMap<>(parentCommentMap.size());

        Page<BaseCommentWithParentVO> pageResult = PageImpl.of(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal());
        // Convert to comment Page
        return pageResult.setContent(commentPage.getContent().stream().map(comment -> {
            // Convert to with parent vo
            BaseCommentWithParentVO commentWithParentVo =
                    new BaseCommentWithParentVO().convertFrom(comment);

            commentWithParentVo.setAvatar(buildAvatarUrl(commentWithParentVo.getGravatarMd5()));

            // Get parent comment vo from cache
            BaseCommentWithParentVO parentCommentVo = parentCommentVoMap.get(comment.getParentId());

            if (parentCommentVo == null) {
                // Get parent comment
                COMMENT parentComment = parentCommentMap.get(comment.getParentId());

                if (parentComment != null) {
                    // Convert to parent comment vo
                    parentCommentVo = new BaseCommentWithParentVO().convertFrom(parentComment);

                    parentCommentVo.setAvatar(buildAvatarUrl(parentComment.getGravatarMd5()));

                    // Cache the parent comment vo
                    parentCommentVoMap.put(parentComment.getId(), parentCommentVo);
                }
            }

            // Set parent
            commentWithParentVo.setParent(parentCommentVo == null ? null : parentCommentVo.clone());

            return commentWithParentVo;
        }).collect(Collectors.toList()));
    }

    @Override
    @NonNull
    public Map<Integer, Long> countByPostIds(Collection<Integer> postIds) {
        if (CollectionUtils.isEmpty(postIds)) {
            return Collections.emptyMap();
        }

        // Get all comment counts
        List<CommentCountProjection> commentCountProjections =
                baseCommentRepository.countByPostIds(postIds);

        return ServiceUtils.convertToMap(commentCountProjections, (cm -> cm.getPostId()),
                CommentCountProjection::getCount);
    }

    @Override
    public Map<Integer, Long> countByStatusAndPostIds(@NonNull CommentStatus status,
                                                      @NonNull Collection<Integer> postIds) {
        if (CollectionUtils.isEmpty(postIds)) {
            return Collections.emptyMap();
        }

        // Get all comment counts
        List<CommentCountProjection> commentCountProjections =
                baseCommentRepository.countByStatusAndPostIds(status, postIds);

        return ServiceUtils.convertToMap(commentCountProjections, CommentCountProjection::getPostId,
                CommentCountProjection::getCount);
    }

    @Override
    public long countByPostId(@NonNull Integer postId) {
        Assert.notNull(postId, "Post id must not be null");
        return baseCommentRepository.countByPostId(postId);
    }

    @Override
    public long countByStatusAndPostId(@NonNull CommentStatus status, @NonNull Integer postId) {
        Assert.notNull(postId, "Post id must not be null");
        return baseCommentRepository.countByStatusAndPostId(status, postId);
    }

    @Override
    public long countByStatus(@NonNull CommentStatus status) {
        return baseCommentRepository.countByStatus(status);
    }

    @Override
    @NonNull
    public COMMENT create(@NonNull COMMENT comment) {
        Assert.notNull(comment, "Domain must not be null");

        // Check post id
        if (!ServiceUtils.isEmptyId(comment.getPostId())) {
            validateTarget(comment.getPostId());
        }

        // Check parent id
        if (!ServiceUtils.isEmptyId(comment.getParentId())) {
            mustExistById(comment.getParentId());
        }

        // Check user login status and set this field
        final Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        // Set some default values
        if (comment.getIpAddress() == null) {
            comment.setIpAddress(ServletUtils.getRequestIp());
        }

        if (comment.getUserAgent() == null) {
            comment.setUserAgent(ServletUtils.getHeaderIgnoreCase(HttpHeaders.USER_AGENT));
        }

        if (comment.getGravatarMd5() == null) {
            comment.setGravatarMd5(
                    DigestUtils.md5Hex(Optional.ofNullable(comment.getEmail()).orElse("")));
        }

        if (StringUtils.isNotEmpty(comment.getAuthorUrl())) {
            comment.setAuthorUrl(HaloUtils.normalizeUrl(comment.getAuthorUrl()));
        }

        if (authentication != null) {
            // Comment of blogger
            comment.setIsAdmin(true);
            comment.setStatus(CommentStatus.PUBLISHED);
        } else {
            // Comment of guest
            // Handle comment status
            Boolean needAudit = optionService
                    .getByPropertyOrDefault(CommentProperties.NEW_NEED_CHECK, Boolean.class, true);
            comment.setStatus(needAudit ? CommentStatus.AUDITING : CommentStatus.PUBLISHED);
        }

        // Create comment
        COMMENT createdComment = super.create(comment);

        if (ServiceUtils.isEmptyId(createdComment.getParentId())) {
            if (authentication == null) {
                // New comment of guest
                eventPublisher.publishEvent(new CommentNewEvent(this, createdComment.getId()));
            }
        } else {
            // Reply comment
            eventPublisher.publishEvent(new CommentReplyEvent(this, createdComment.getId()));
        }

        return createdComment;
    }

    @Override
    @NonNull
    public COMMENT createBy(@NonNull BaseCommentParam<COMMENT> commentParam) {
        Assert.notNull(commentParam, "Comment param must not be null");

        // Check user login status and set this field
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            // Blogger comment
            User user = authentication.getDetail().getUser();
            commentParam.setAuthor(
                    StringUtils.isBlank(user.getNickname()) ? user.getUsername() : user.getNickname());
            commentParam.setEmail(user.getEmail());
            commentParam.setAuthorUrl(
                    optionService.getByPropertyOrDefault(BlogProperties.BLOG_URL, String.class, null));
        }

        // Validate the comment param manually
        ValidationUtils.validate(commentParam);

        if (authentication == null) {
            // Anonymous comment
            // Check email
            if (userService.getByEmail(commentParam.getEmail()).isPresent()) {
                throw new BadRequestException("不能使用博主的邮箱，如果您是博主，请登录管理端进行回复。");
            }
        }

        // Convert to comment
        return create(commentParam.convertTo());
    }

    @Override
    @NonNull
    public COMMENT updateStatus(@NonNull Long commentId, @NonNull CommentStatus status) {
        Assert.notNull(commentId, "Comment id must not be null");
        Assert.notNull(status, "Comment status must not be null");

        // Get comment by id
        COMMENT comment = getById(commentId);

        // Set comment status
        comment.setStatus(status);

        // Update comment
        return update(comment);
    }

    @Override
    @NonNull
    public List<COMMENT> updateStatusByIds(@NonNull List<Long> ids, @NonNull CommentStatus status) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return ids.stream().map(id -> {
            return updateStatus(id, status);
        }).collect(Collectors.toList());
    }

    @Override
    public List<COMMENT> removeByPostId(@NonNull Integer postId) {
        Assert.notNull(postId, "Post id must not be null");
        List<COMMENT> allByPostId = baseCommentRepository.findAllByPostId(postId);
        baseCommentRepository.deleteByPostId(postId);
        return allByPostId;
    }

    @Override
    @NonNull
    public COMMENT removeById(@NonNull Long id) {
        Assert.notNull(id, "Comment id must not be null");

        COMMENT comment = Optional.ofNullable(baseCommentRepository.selectById(id))
                .orElseThrow(() -> new NotFoundException("查询不到该评论的信息").setErrorData(id));

        List<COMMENT> children =
                listChildrenBy(comment.getPostId(), id, Sort.by(DESC, "create_time"));

        if (children.size() > 0) {
            children.forEach(child -> {
                super.removeById(child.getId());
            });
        }

        return super.removeById(id);
    }

    @Override
    @NonNull
    public List<COMMENT> removeByIds(@NonNull Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return ids.stream().map(this::removeById).collect(Collectors.toList());
    }

    @Override
    @NonNull
    public List<BaseCommentDTO> convertTo(@NonNull List<COMMENT> comments) {
        if (CollectionUtils.isEmpty(comments)) {
            return Collections.emptyList();
        }
        return comments.stream()
                .map(this::convertTo)
                .collect(Collectors.toList());
    }

    @Override
    @NonNull
    public Page<BaseCommentDTO> convertTo(@NonNull Page<COMMENT> commentPage) {
        Assert.notNull(commentPage, "Comment Page must not be null");
        Page<BaseCommentDTO> resultPage = PageImpl.of(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal());

        return resultPage.setContent(commentPage.getContent().stream().map(cm -> convertTo(cm)).collect(Collectors.toList()));
    }

    @Override
    @NonNull
    public BaseCommentDTO convertTo(@NonNull COMMENT comment) {
        Assert.notNull(comment, "Comment must not be null");

        BaseCommentDTO baseCommentDto = new BaseCommentDTO().convertFrom(comment);

        baseCommentDto.setAvatar(buildAvatarUrl(comment.getGravatarMd5()));

        return baseCommentDto;
    }


    /**
     * Builds a comment comparator.
     *
     * @param sort sort info
     * @return comment comparator
     */
    protected Comparator<BaseCommentVO> buildCommentComparator(Sort sort) {
        return (currentComment, toCompareComment) -> {
            Assert.notNull(currentComment, "Current comment must not be null");
            Assert.notNull(toCompareComment, "Comment to compare must not be null");

            // Get sort order
            Sort.Order order = sort.filter(anOrder -> "id".equals(anOrder.getProperty()))
                    .get()
                    .findFirst()
                    .orElseGet(() -> Sort.Order.desc("id"));

            // Init sign
            int sign = order.getDirection() == ASC ? 1 : -1;

            // Compare id property
            return sign * currentComment.getId().compareTo(toCompareComment.getId());
        };
    }

    @NonNull
    @Override
    public List<BaseCommentVO> convertToVo(@Nullable List<COMMENT> comments,
                                           @Nullable Comparator<BaseCommentVO> comparator) {
        if (CollectionUtils.isEmpty(comments)) {
            return Collections.emptyList();
        }

        // Init the top virtual comment
        BaseCommentVO topVirtualComment = new BaseCommentVO();
        topVirtualComment.setId(0L);
        topVirtualComment.setChildren(new LinkedList<>());

        // Concrete the comment tree
        concreteTree(topVirtualComment, new LinkedList<>(comments), comparator);

        return topVirtualComment.getChildren();
    }

    @Override
    @NonNull
    public Page<CommentWithHasChildrenVO> pageTopCommentsBy(@NonNull Integer targetId,
                                                            @NonNull CommentStatus status,
                                                            @NonNull Page page) {
        Assert.notNull(targetId, "Target id must not be null");
        Assert.notNull(status, "Comment status must not be null");
        Assert.notNull(page, "Page info must not be null");

        // Get all comments
        Page<COMMENT> topCommentPage = MybatisPageHelper.fillPageData(baseCommentRepository
                .findPageByPostIdAndStatusAndParentId(targetId, status, 0L,
                        MybatisPageHelper.changeToMybatisPage(page)), page);

        if (topCommentPage.getTotal() == 0) {
            // If the comments is empty
            return ServiceUtils.buildEmptyPageImpl(topCommentPage);
        }

        // Get top comment ids
        Set<Long> topCommentIds =
                ServiceUtils.fetchProperty(topCommentPage.getContent(), BaseComment::getId);

        // Get direct children count
        List<CommentChildrenCountProjection> directChildrenCount =
                baseCommentRepository.findDirectChildrenCount(topCommentIds, CommentStatus.PUBLISHED);

        // Convert to comment - children count map
        Map<Long, Long> commentChildrenCountMap = ServiceUtils
                .convertToMap(directChildrenCount, CommentChildrenCountProjection::getCommentId,
                        CommentChildrenCountProjection::getDirectChildrenCount);

        Page<CommentWithHasChildrenVO> pageResult = PageImpl.of(topCommentPage.getCurrent(), topCommentPage.getSize(), topCommentPage.getTotal());

        // Convert to comment with has children vo
        return pageResult.setContent(topCommentPage.getContent().stream().map(
                topComment -> {
                    CommentWithHasChildrenVO comment =
                            new CommentWithHasChildrenVO().convertFrom(topComment);
                    comment
                            .setHasChildren(commentChildrenCountMap.getOrDefault(topComment.getId(), 0L) > 0);
                    comment.setAvatar(buildAvatarUrl(topComment.getGravatarMd5()));
                    return comment;
                }).collect(Collectors.toList()));
    }

    @Override
    @NonNull
    public List<COMMENT> listChildrenBy(@NonNull Integer targetId, @NonNull Long commentParentId,
                                        @NonNull CommentStatus status, @NonNull Sort sort) {
        Assert.notNull(targetId, "Target id must not be null");
        Assert.notNull(commentParentId, "Comment parent id must not be null");
        Assert.notNull(sort, "Sort info must not be null");

        // Get comments recursively

        // Get direct children
        List<COMMENT> directChildren = baseCommentRepository
                .findAllByPostIdAndStatusAndParentId(targetId, status, commentParentId);

        // Create result container
        Set<COMMENT> children = new HashSet<>();

        // Get children comments
        getChildrenRecursively(directChildren, status, children);

        // Sort children
        List<COMMENT> childrenList = new ArrayList<>(children);
        childrenList.sort(Comparator.comparing(BaseComment::getId));

        return childrenList;
    }

    @Override
    @NonNull
    public List<COMMENT> listChildrenBy(@NonNull Integer targetId, @NonNull Long commentParentId,
                                        @NonNull Sort sort) {
        Assert.notNull(targetId, "Target id must not be null");
        Assert.notNull(commentParentId, "Comment parent id must not be null");
        Assert.notNull(sort, "Sort info must not be null");

        // Get comments recursively

        // Get direct children
        List<COMMENT> directChildren =
                baseCommentRepository.findAllByPostIdAndParentId(targetId, commentParentId);

        // Create result container
        Set<COMMENT> children = new HashSet<>();

        // Get children comments
        getChildrenRecursively(directChildren, children);

        // Sort children
        List<COMMENT> childrenList = new ArrayList<>(children);
        childrenList.sort(Comparator.comparing(BaseComment::getId));

        return childrenList;
    }

    /**
     * Get children comments recursively.
     *
     * @param topComments top comment list
     * @param status      comment status must not be null
     * @param children    children result must not be null
     */
    private void getChildrenRecursively(@Nullable List<COMMENT> topComments,
                                        @NonNull CommentStatus status, @NonNull Set<COMMENT> children) {
        Assert.notNull(status, "Comment status must not be null");
        Assert.notNull(children, "Children comment set must not be null");

        if (CollectionUtils.isEmpty(topComments)) {
            return;
        }

        // Convert comment id set
        Set<Long> commentIds = ServiceUtils.fetchProperty(topComments, COMMENT::getId);

        // Get direct children
        List<COMMENT> directChildren =
                baseCommentRepository.findAllByStatusAndParentIdIn(status, commentIds);

        // Recursively invoke
        getChildrenRecursively(directChildren, status, children);

        // Add direct children to children result
        children.addAll(topComments);
    }

    /**
     * Get children comments recursively.
     *
     * @param topComments top comment list
     * @param children    children result must not be null
     */
    private void getChildrenRecursively(@Nullable List<COMMENT> topComments,
                                        @NonNull Set<COMMENT> children) {
        Assert.notNull(children, "Children comment set must not be null");

        if (CollectionUtils.isEmpty(topComments)) {
            return;
        }

        // Convert comment id set
        Set<Long> commentIds = ServiceUtils.fetchProperty(topComments, COMMENT::getId);

        // Get direct children
        List<COMMENT> directChildren = baseCommentRepository.findAllByParentIdIn(commentIds);

        // Recursively invoke
        getChildrenRecursively(directChildren, children);

        // Add direct children to children result
        children.addAll(topComments);
    }

    /**
     * Concretes comment tree.
     *
     * @param parentComment     parent comment vo must not be null
     * @param comments          comment list must not null
     * @param commentComparator comment vo comparator
     */
    protected void concreteTree(@NonNull BaseCommentVO parentComment,
                                @Nullable Collection<COMMENT> comments,
                                @Nullable Comparator<BaseCommentVO> commentComparator) {
        Assert.notNull(parentComment, "Parent comment must not be null");

        if (CollectionUtils.isEmpty(comments)) {
            return;
        }

        // Get children
        List<COMMENT> children = comments.stream()
                .filter(comment -> Objects.equals(parentComment.getId(), comment.getParentId()))
                .collect(Collectors.toList());

        // Add children
        children.forEach(comment -> {
            // Convert to comment vo
            BaseCommentVO commentVo = new BaseCommentVO().convertFrom(comment);

            commentVo.setAvatar(buildAvatarUrl(commentVo.getGravatarMd5()));

            if (parentComment.getChildren() == null) {
                parentComment.setChildren(new LinkedList<>());
            }

            parentComment.getChildren().add(commentVo);
        });

        // Remove children
        comments.removeAll(children);

        if (!CollectionUtils.isEmpty(parentComment.getChildren())) {
            // Recursively concrete the children
            parentComment.getChildren()
                    .forEach(childComment -> concreteTree(childComment, comments, commentComparator));
            // Sort the children
            if (commentComparator != null) {
                parentComment.getChildren().sort(commentComparator);
            }
        }
    }

    /**
     * Build avatar url by gravatarMd5
     *
     * @param gravatarMd5 gravatarMd5
     * @return avatar url
     */
    public String buildAvatarUrl(String gravatarMd5) {
        final String gravatarSource =
                optionService.getByPropertyOrDefault(CommentProperties.GRAVATAR_SOURCE, String.class);
        final String gravatarDefault =
                optionService.getByPropertyOrDefault(CommentProperties.GRAVATAR_DEFAULT, String.class);

        return gravatarSource + gravatarMd5 + "?s=256&d=" + gravatarDefault;
    }
}
