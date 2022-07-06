package com.tmser.blog.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import com.tmser.blog.model.dto.BaseMetaDTO;
import com.tmser.blog.model.entity.BaseMeta;
import com.tmser.blog.model.params.BaseMetaParam;
import com.tmser.blog.repository.base.BaseMetaRepository;
import com.tmser.blog.service.base.AbstractCrudService;
import com.tmser.blog.service.base.BaseMetaService;
import com.tmser.blog.utils.ServiceUtils;

/**
 * Base meta service implementation.
 *
 * @author ryanwang
 * @author ikaisec
 * @date 2019-08-04
 */
@Slf4j
public abstract class BaseMetaServiceImpl<META extends BaseMeta>
    extends AbstractCrudService<META,Integer> implements BaseMetaService<META> {

    protected final BaseMetaRepository<META> baseMetaRepository;


    public BaseMetaServiceImpl(BaseMetaRepository<META> baseMetaRepository) {
        super(baseMetaRepository);
        this.baseMetaRepository = baseMetaRepository;
    }

    @Override
    @Transactional
    public List<META> createOrUpdateByPostId(@NonNull Integer postId, Set<META> metas) {
        Assert.notNull(postId, "Post id must not be null");

        // firstly remove post metas by post id
        removeByPostId(postId);

        if (CollectionUtils.isEmpty(metas)) {
            return Collections.emptyList();
        }

        // Save post metas
        metas.forEach(postMeta -> {
            if (StringUtils.isNotEmpty(postMeta.getValue())
                && StringUtils.isNotEmpty(postMeta.getKey())) {
                postMeta.setPostId(postId);
                create(postMeta);
            }
        });
        return new ArrayList<>(metas);
    }

    @Override
    public List<META> removeByPostId(@NonNull Integer postId) {
        Assert.notNull(postId, "Post id must not be null of removeByPostId");
        List<META> allByPostId = baseMetaRepository.findAllByPostId(postId);
        baseMetaRepository.deleteByPostId(postId);
        return allByPostId;
    }

    @Override
    public Map<Integer, List<META>> listPostMetaAsMap(@NonNull Set<Integer> postIds) {
        Assert.notNull(postIds, "Post ids must not be null");
        if (CollectionUtils.isEmpty(postIds)) {
            return Collections.emptyMap();
        }

        // Find all metas
        List<META> metas = baseMetaRepository.findAllByPostIdIn(postIds);

        // Convert to meta map
        Map<Long, META> postMetaMap = ServiceUtils.convertToMap(metas, META::getId);

        // Create category list map
        Map<Integer, List<META>> postMetaListMap = new HashMap<>();

        // Foreach and collect
        metas.forEach(
            meta -> postMetaListMap.computeIfAbsent(meta.getPostId(), postId -> new LinkedList<>())
                .add(postMetaMap.get(meta.getId())));

        return postMetaListMap;
    }

    @Override
    public @NonNull List<META> listBy(@NonNull Integer postId) {
        Assert.notNull(postId, "Post id must not be null");
        return baseMetaRepository.findAllByPostId(postId);
    }

    @Override
    public @NonNull META create(@NonNull META meta) {
        Assert.notNull(meta, "Domain must not be null");

        // Check post id
        if (!ServiceUtils.isEmptyId(meta.getPostId())) {
            validateTarget(meta.getPostId());
        }

        // Create meta
        return super.create(meta);
    }

    @Override
    public @NonNull META createBy(@NonNull BaseMetaParam<META> metaParam) {
        Assert.notNull(metaParam, "Meta param must not be null");
        return create(metaParam.convertTo());
    }

    @Override
    public Map<String, Object> convertToMap(List<META> metas) {
        return ServiceUtils.convertToMap(metas, META::getKey, META::getValue);
    }


    @Override
    public @NonNull BaseMetaDTO convertTo(@NonNull META postMeta) {
        Assert.notNull(postMeta, "Category must not be null");

        return new BaseMetaDTO().convertFrom(postMeta);
    }

    @Override
    public @NonNull List<BaseMetaDTO> convertTo(@NonNull List<META> postMetaList) {
        if (CollectionUtils.isEmpty(postMetaList)) {
            return Collections.emptyList();
        }

        return postMetaList.stream()
            .map(this::convertTo)
            .collect(Collectors.toList());
    }
}
