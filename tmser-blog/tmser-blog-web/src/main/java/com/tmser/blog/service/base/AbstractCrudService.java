package com.tmser.blog.service.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.google.common.collect.Lists;
import com.tmser.blog.exception.NotFoundException;
import com.tmser.blog.repository.base.BaseRepository;
import com.tmser.database.mybatis.MybatisPageHelper;
import com.tmser.model.page.Page;
import com.tmser.model.page.Pageable;
import com.tmser.model.sort.Sort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Abstract service implementation.
 *
 * @param <DOMAIN> domain type
 * @author johnniang
 */
@Slf4j
public abstract class AbstractCrudService<DOMAIN, I extends Serializable> implements CrudService<DOMAIN, I> {

    private final String domainName;


    private final BaseRepository<DOMAIN> repository;

    protected AbstractCrudService(BaseRepository<DOMAIN> repository) {
        this.repository = repository;

        // Get domain name
        @SuppressWarnings("unchecked")
        Class<DOMAIN> domainClass = (Class<DOMAIN>) fetchType(0);
        domainName = domainClass.getSimpleName();
    }

    /**
     * Gets actual generic type.
     *
     * @param index generic type index
     * @return real generic type will be returned
     */
    private Type fetchType(int index) {
        Assert.isTrue(index >= 0 && index <= 1, "type index must be between 0 to 1");

        return ((ParameterizedType) this.getClass().getGenericSuperclass())
                .getActualTypeArguments()[index];
    }

    /**
     * List All
     *
     * @return List
     */
    @Override
    public List<DOMAIN> listAll() {
        return repository.selectList(null);
    }

    /**
     * List all by sort
     *
     * @param sort sort
     * @return List
     */
    @Override
    public List<DOMAIN> listAll(Sort sort) {
        Assert.notNull(sort, "Sort info must not be null");
        final QueryWrapper<DOMAIN> domainQueryWrapper = new QueryWrapper<>();
        sort.stream().forEach(orderItem -> {
            domainQueryWrapper.orderBy(true, orderItem.getDirection() == Sort.Direction.ASC, orderItem.getProperty());
        });
        return repository.selectList(domainQueryWrapper);
    }

    /**
     * List all by pageable
     *
     * @param pageable pageable
     * @return Page
     */
    @Override
    public Page<DOMAIN> listAll(Pageable pageable) {
        Assert.notNull(pageable, "Pageable info must not be null");

        return MybatisPageHelper.fillPageData(
                repository.selectPage(MybatisPageHelper.changeToMybatisPage(pageable), null), pageable);
    }

    /**
     * List all by ids
     *
     * @param ids ids
     * @return List
     */
    @Override
    public List<DOMAIN> listAllByIds(Collection<I> ids) {
        return CollectionUtils.isEmpty(ids) ? Collections.emptyList() : repository.selectBatchIds((Collection<? extends Serializable>) ids);
    }

    /**
     * List all by ids and sort
     *
     * @param ids  ids
     * @param sort sort
     * @return List
     */
    @Override
    public List<DOMAIN> listAllByIds(Collection<I> ids, Sort sort) {
        Assert.notNull(sort, "Sort info must not be null");
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }

        final QueryWrapper<DOMAIN> domainQueryWrapper = new QueryWrapper<>();
        sort.stream().forEach(orderItem -> {
            domainQueryWrapper.orderBy(true, orderItem.getDirection() == Sort.Direction.ASC, orderItem.getProperty());
        });
        domainQueryWrapper.in(TableInfoHelper.getTableInfo(domainQueryWrapper.getEntityClass()).getKeyColumn(), ids);
        return repository.selectList(domainQueryWrapper);
    }

    /**
     * Fetch by id
     *
     * @param id id
     * @return Optional
     */
    @Override
    public Optional<DOMAIN> fetchById(I id) {
        Assert.notNull(id, domainName + " id must not be null");

        return Optional.ofNullable(repository.selectById(id));
    }

    /**
     * Get by id
     *
     * @param id id
     * @return DOMAIN
     * @throws NotFoundException If the specified id does not exist
     */
    @Override
    public DOMAIN getById(I id) {
        return fetchById(id).orElseThrow(
                () -> new NotFoundException(domainName + " was not found or has been deleted"));
    }

    /**
     * Gets domain of nullable by id.
     *
     * @param id id
     * @return DOMAIN
     */
    @Override
    public DOMAIN getByIdOfNullable(I id) {
        return fetchById(id).orElse(null);
    }

    /**
     * Exists by id.
     *
     * @param id id
     * @return boolean
     */
    @Override
    public boolean existsById(I id) {
        Assert.notNull(id, domainName + " id must not be null");

        return fetchById(id).isPresent();
    }

    /**
     * Must exist by id, or throw NotFoundException.
     *
     * @param id id
     * @throws NotFoundException If the specified id does not exist
     */
    @Override
    public void mustExistById(I id) {
        if (!existsById(id)) {
            throw new NotFoundException(domainName + " was not exist");
        }
    }

    /**
     * count all
     *
     * @return long
     */
    @Override
    public long count() {
        return repository.selectCount(null);
    }

    /**
     * save by domain
     *
     * @param domain domain
     * @return DOMAIN
     */
    @Override
    public DOMAIN create(DOMAIN domain) {
        Assert.notNull(domain, domainName + " data must not be null");
        repository.insert(domain);
        return domain;
    }

    /**
     * save by domains
     *
     * @param domains domains
     * @return List
     */
    @Override
    public List<DOMAIN> createInBatch(Collection<DOMAIN> domains) {
        if (CollectionUtils.isEmpty(domains)) {
            return Collections.emptyList();
        }
        domains.stream().forEach(domain -> create(domain));
        return Lists.newArrayList(domains);
    }

    /**
     * Updates by domain
     *
     * @param domain domain
     * @return DOMAIN
     */
    @Override
    public DOMAIN update(DOMAIN domain) {
        Assert.notNull(domain, domainName + " data must not be null");
        repository.updateById(domain);
        return domain;
    }

    /**
     * Updates by domains
     *
     * @param domains domains
     * @return List
     */
    @Override
    public List<DOMAIN> updateInBatch(Collection<DOMAIN> domains) {
        if (CollectionUtils.isEmpty(domains)) {
            return Collections.emptyList();
        }
        domains.stream().forEach(domain -> update(domain));
        return Lists.newArrayList(domains);
    }

    /**
     * Removes by id
     *
     * @param id id
     * @return DOMAIN
     * @throws NotFoundException If the specified id does not exist
     */
    @Override
    public DOMAIN removeById(I id) {
        // Get non null domain by id
        DOMAIN domain = getById(id);
        // Remove it
        repository.deleteById(id);
        // return the deleted domain
        return domain;
    }

    /**
     * Removes by id if present.
     *
     * @param id id
     * @return DOMAIN
     */
    @Override
    public DOMAIN removeByIdOfNullable(I id) {
        return fetchById(id).map(domain -> {
            return removeById(id);
        }).orElse(null);
    }

    /**
     * Remove by ids
     *
     * @param ids ids
     */
    @Override
    public void removeInBatch(Collection<I> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            log.debug(domainName + " id collection is empty");
            return;
        }

        repository.deleteBatchIds(ids);
    }


    /**
     * Remove all
     */
    @Override
    public void removeAll() {
        repository.delete(null);
    }
}
