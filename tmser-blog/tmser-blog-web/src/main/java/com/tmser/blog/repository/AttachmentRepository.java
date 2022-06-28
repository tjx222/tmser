package com.tmser.blog.repository;

import com.tmser.blog.model.entity.Attachment;
import com.tmser.blog.model.enums.AttachmentType;
import com.tmser.blog.repository.base.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * Attachment repository
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-04-03
 */
@Mapper
public interface AttachmentRepository
        extends BaseRepository<Attachment> {

    /**
     * Find all attachment media type.
     *
     * @return list of media type.
     */
    @Select(value = "select distinct a.mediaType from Attachment a")
    List<String> findAllMediaType();

    /**
     * Find all attachment type.
     *
     * @return list of type.
     */
    @Select(value = "select distinct a.type from Attachment a")
    List<AttachmentType> findAllType();

    /**
     * Counts by attachment path.
     *
     * @param path attachment path must not be blank
     * @return count of the given path
     */
    long countByPath(@NonNull String path);

    /**
     * Counts by attachment file key and type.
     *
     * @param fileKey attachment file key must not be blank
     * @param type    attachment type must not be null
     * @return count of the given path and type
     */
    long countByFileKeyAndType(@NonNull String fileKey, @NonNull AttachmentType type);
}