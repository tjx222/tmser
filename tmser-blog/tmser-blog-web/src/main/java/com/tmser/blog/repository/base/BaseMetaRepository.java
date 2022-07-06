package com.tmser.blog.repository.base;

import com.tmser.blog.model.entity.BaseMeta;
import com.tmser.blog.model.entity.SheetMeta;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Set;

/**
 * Base meta repository.
 *
 * @author ryanwang
 * @author ikaisec
 * @date 2019-08-04
 */
public interface BaseMetaRepository<M extends BaseMeta>
        extends BaseRepository<M> {

    List<M> findAllByPostId(@NonNull Integer postId);

    /**
     * Deletes post metas by post id.
     *
     * @param postId post id must not be null
     * @return a list of post meta deleted
     */
    void deleteByPostId(@NonNull Integer postId);

    /**
     * Finds all post metas by post id.
     *
     * @param postIds post id must not be null
     * @return a list of post meta
     */
    List<M> findAllByPostIdIn(@NonNull Set<Integer> postIds);

}
