package com.tmser.blog.repository;

import com.tmser.blog.model.entity.Sheet;
import com.tmser.blog.model.enums.PostStatus;
import com.tmser.blog.repository.base.BasePostRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.lang.NonNull;

import java.util.Optional;

/**
 * Sheet repository.
 *
 * @author johnniang
 * @date 3/22/19
 */
@Mapper
public interface SheetRepository extends BasePostRepository<Sheet> {

    /**
     * Count all sheet visits.
     *
     * @return visits.
     */
    @Override
    @Select("select sum(p.visits) from Sheet p")
    Long countVisit();

    /**
     * Count all sheet likes.
     *
     * @return likes.
     */
    @Override
    @Select("select sum(p.likes) from Sheet p")
    Long countLike();

    /**
     * Gets sheet by slug and status.
     *
     * @param slug   slug must not be blank
     * @param status status must not be null
     * @return an optional of sheet.
     */
    @NonNull
    @Override
    Optional<Sheet> getBySlugAndStatus(@NonNull String slug, @NonNull PostStatus status);
}
