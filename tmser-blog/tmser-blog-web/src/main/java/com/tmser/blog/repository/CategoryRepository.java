package com.tmser.blog.repository;

import com.tmser.blog.model.entity.Category;
import com.tmser.blog.repository.base.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Category repository.
 *
 * @author johnniang
 */
@Mapper
public interface CategoryRepository extends BaseRepository<Category> {

    /**
     * Counts by category name.
     *
     * @param name category name must not be blank
     * @return the count
     */
    long countByName(@NonNull String name);

    /**
     * Counts by category id.
     *
     * @param id category id must not be null
     * @return the count
     */
    long countById(@NonNull Integer id);

    /**
     * Get category by slug
     *
     * @param slug slug
     * @return Optional of Category
     */
    Optional<Category> getBySlug(@NonNull String slug);

    /**
     * Get category by name.
     *
     * @param name name
     * @return Optional of Category
     */
    Optional<Category> getByName(@NonNull String name);

    /**
     * List categories by parent id.
     *
     * @param id parent id.
     * @return list of category
     */
    List<Category> findByParentId(@NonNull Serializable id);
}
