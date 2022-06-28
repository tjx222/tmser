package com.tmser.blog.service.impl;

import com.tmser.blog.exception.BadRequestException;
import com.tmser.blog.exception.ForbiddenException;
import com.tmser.blog.exception.NotFoundException;
import com.tmser.blog.model.dto.post.BasePostMinimalDTO;
import com.tmser.blog.model.entity.Post;
import com.tmser.blog.model.entity.PostComment;
import com.tmser.blog.model.enums.CommentViolationTypeEnum;
import com.tmser.blog.model.enums.PostPermalinkType;
import com.tmser.blog.model.properties.CommentProperties;
import com.tmser.blog.model.vo.PostCommentWithPostVO;
import com.tmser.blog.repository.PostCommentRepository;
import com.tmser.blog.repository.PostRepository;
import com.tmser.blog.service.CommentBlackListService;
import com.tmser.blog.service.OptionService;
import com.tmser.blog.service.PostCommentService;
import com.tmser.blog.service.UserService;
import com.tmser.blog.utils.DateUtils;
import com.tmser.blog.utils.ServiceUtils;
import com.tmser.blog.utils.ServletUtils;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.tmser.blog.model.support.HaloConst.URL_SEPARATOR;

/**
 * PostCommentService implementation class
 *
 * @author ryanwang
 * @author johnniang
 * @date 2019-03-14
 */
@Slf4j
@Service
public class PostCommentServiceImpl extends BaseCommentServiceImpl<PostComment>
        implements PostCommentService {

    private final PostRepository postRepository;

    private final CommentBlackListService commentBlackListService;

    public PostCommentServiceImpl(PostCommentRepository postCommentRepository,
                                  PostRepository postRepository,
                                  UserService userService,
                                  OptionService optionService,
                                  CommentBlackListService commentBlackListService,
                                  ApplicationEventPublisher eventPublisher) {
        super(postCommentRepository, optionService, userService, eventPublisher);
        this.postRepository = postRepository;
        this.commentBlackListService = commentBlackListService;
    }

    @Override
    @NonNull
    public Page<PostCommentWithPostVO> convertToWithPostVo(@NonNull Page<PostComment> commentPage) {
        Assert.notNull(commentPage, "PostComment page must not be null");
        Page<PostCommentWithPostVO> pageResult = PageImpl.of(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal());
        return pageResult.setContent(convertToWithPostVo(commentPage.getContent()));

    }

    @Override
    @NonNull
    public PostCommentWithPostVO convertToWithPostVo(@NonNull PostComment comment) {
        Assert.notNull(comment, "PostComment must not be null");
        PostCommentWithPostVO postCommentWithPostVo =
                new PostCommentWithPostVO().convertFrom(comment);

        BasePostMinimalDTO basePostMinimalDto =
                new BasePostMinimalDTO().convertFrom(postRepository.selectById(comment.getPostId()));

        postCommentWithPostVo.setPost(buildPostFullPath(basePostMinimalDto));

        postCommentWithPostVo.setAvatar(buildAvatarUrl(comment.getGravatarMd5()));

        return postCommentWithPostVo;
    }

    @Override
    @NonNull
    public List<PostCommentWithPostVO> convertToWithPostVo(List<PostComment> postComments) {
        if (CollectionUtils.isEmpty(postComments)) {
            return Collections.emptyList();
        }

        // Fetch goods ids
        Set<Integer> postIds = ServiceUtils.fetchProperty(postComments, PostComment::getPostId);

        // Get all posts
        Map<Integer, Post> postMap =
                ServiceUtils.convertToMap(postRepository.selectBatchIds(postIds), Post::getId);

        return postComments.stream()
                .filter(comment -> postMap.containsKey(comment.getPostId()))
                .map(comment -> {
                    // Convert to vo
                    PostCommentWithPostVO postCommentWithPostVo =
                            new PostCommentWithPostVO().convertFrom(comment);

                    BasePostMinimalDTO basePostMinimalDto =
                            new BasePostMinimalDTO().convertFrom(postMap.get(comment.getPostId()));

                    postCommentWithPostVo.setPost(buildPostFullPath(basePostMinimalDto));

                    postCommentWithPostVo.setAvatar(buildAvatarUrl(comment.getGravatarMd5()));

                    return postCommentWithPostVo;
                }).collect(Collectors.toList());
    }

    private BasePostMinimalDTO buildPostFullPath(BasePostMinimalDTO post) {
        PostPermalinkType permalinkType = optionService.getPostPermalinkType();

        String pathSuffix = optionService.getPathSuffix();

        String archivesPrefix = optionService.getArchivesPrefix();

        int month = DateUtils.month(post.getCreateTime()) + 1;

        String monthString = month < 10 ? "0" + month : String.valueOf(month);

        int day = DateUtils.dayOfMonth(post.getCreateTime());

        String dayString = day < 10 ? "0" + day : String.valueOf(day);

        StringBuilder fullPath = new StringBuilder();

        if (optionService.isEnabledAbsolutePath()) {
            fullPath.append(optionService.getBlogBaseUrl());
        }

        fullPath.append(URL_SEPARATOR);

        if (permalinkType.equals(PostPermalinkType.DEFAULT)) {
            fullPath.append(archivesPrefix)
                    .append(URL_SEPARATOR)
                    .append(post.getSlug())
                    .append(pathSuffix);
        } else if (permalinkType.equals(PostPermalinkType.ID)) {
            fullPath.append("?p=")
                    .append(post.getId());
        } else if (permalinkType.equals(PostPermalinkType.DATE)) {
            fullPath.append(DateUtils.year(post.getCreateTime()))
                    .append(URL_SEPARATOR)
                    .append(monthString)
                    .append(URL_SEPARATOR)
                    .append(post.getSlug())
                    .append(pathSuffix);
        } else if (permalinkType.equals(PostPermalinkType.DAY)) {
            fullPath.append(DateUtils.year(post.getCreateTime()))
                    .append(URL_SEPARATOR)
                    .append(monthString)
                    .append(URL_SEPARATOR)
                    .append(dayString)
                    .append(URL_SEPARATOR)
                    .append(post.getSlug())
                    .append(pathSuffix);
        } else if (permalinkType.equals(PostPermalinkType.YEAR)) {
            fullPath.append(DateUtils.year(post.getCreateTime()))
                    .append(URL_SEPARATOR)
                    .append(post.getSlug())
                    .append(pathSuffix);
        } else if (permalinkType.equals(PostPermalinkType.ID_SLUG)) {
            fullPath.append(archivesPrefix)
                    .append(URL_SEPARATOR)
                    .append(post.getId())
                    .append(pathSuffix);
        }

        post.setFullPath(fullPath.toString());

        return post;
    }

    @Override
    public void validateTarget(@NonNull Integer postId) {
        Post post = Optional.ofNullable(postRepository.selectById(postId))
                .orElseThrow(() -> new NotFoundException("查询不到该文章的信息").setErrorData(postId));

        if (post.getDisallowComment()) {
            throw new BadRequestException("该文章已经被禁止评论").setErrorData(postId);
        }
    }

    @Override
    public void validateCommentBlackListStatus() {
        CommentViolationTypeEnum banStatus =
                commentBlackListService.commentsBanStatus(ServletUtils.getRequestIp());
        Integer banTime = optionService
                .getByPropertyOrDefault(CommentProperties.COMMENT_BAN_TIME, Integer.class, 10);
        if (banStatus == CommentViolationTypeEnum.FREQUENTLY) {
            throw new ForbiddenException(String.format("您的评论过于频繁，请%s分钟之后再试。", banTime));
        }
    }

}