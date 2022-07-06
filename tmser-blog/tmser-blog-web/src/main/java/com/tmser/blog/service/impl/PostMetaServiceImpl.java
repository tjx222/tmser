package com.tmser.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tmser.blog.model.entity.SheetMeta;
import com.tmser.blog.repository.PostMetaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import com.tmser.blog.exception.NotFoundException;
import com.tmser.blog.model.entity.PostMeta;
import com.tmser.blog.repository.PostRepository;
import com.tmser.blog.repository.base.BaseMetaRepository;
import com.tmser.blog.service.PostMetaService;

import java.util.List;
import java.util.Optional;

/**
 * Post meta service implementation class.
 *
 * @author ryanwang
 * @author ikaisec
 * @author guqing
 * @date 2019-08-04
 */
@Slf4j
@Service
public class PostMetaServiceImpl extends BaseMetaServiceImpl<PostMeta> implements PostMetaService {

    private final PostRepository postRepository;

    private final PostMetaRepository postMetaRepository;

    public PostMetaServiceImpl(PostMetaRepository postMetaRepository,
        PostRepository postRepository) {
        super(postMetaRepository);
        this.postMetaRepository = postMetaRepository;
        this.postRepository = postRepository;
    }

    /**
     * List All
     *
     * @return List
     */
    @Override
    public List<PostMeta> listAll() {
        return postMetaRepository.selectList(new QueryWrapper<PostMeta>().eq(true,"type", SheetMeta.MT_POST));
    }


    @Override
    public void validateTarget(@NonNull Integer postId) {
        Optional.ofNullable(postRepository.selectById(postId))
            .orElseThrow(() -> new NotFoundException("查询不到该文章的信息").setErrorData(postId));
    }
}
