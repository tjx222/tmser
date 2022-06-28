package com.tmser.blog.repository;

import com.tmser.blog.model.entity.Tag;
import com.tmser.blog.repository.base.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.lang.NonNull;

import java.util.Optional;

/**
 * Tag repository.
 *
 * @author johnniang
 */
@Mapper
public interface TagRepository extends BaseRepository<Tag> {

    /**
     * Count by name or slug.
     *
     * @param name tag name must not be null
     * @param slug tag slug must not be null
     * @return tag count
     */
    long countByNameOrSlug(@NonNull String name, @NonNull String slug);

    /**
     * Get tag by slug
     *
     * @param slug slug must not be null.
     * @return an optional of slug.
     */
    Optional<Tag> getBySlug(@NonNull String slug);

    /**
     * Get tag by name
     *
     * @param name name must not be null.
     * @return an optional of tag
     */
    Optional<Tag> getByName(@NonNull String name);
}
