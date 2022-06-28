package com.tmser.blog.service;

import com.tmser.blog.exception.FileOperationException;
import com.tmser.blog.model.dto.AttachmentDTO;
import com.tmser.blog.model.entity.Attachment;
import com.tmser.blog.model.enums.AttachmentType;
import com.tmser.blog.model.params.AttachmentQuery;
import com.tmser.blog.service.base.CrudService;
import com.tmser.model.page.Page;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;


/**
 * Attachment service.
 *
 * @author johnniang
 * @date 2019-03-14
 */
public interface AttachmentService extends CrudService<Attachment, Integer> {

    /**
     * Pages attachment output dtos.
     *
     * @param pageable        Page info must not be null
     * @param attachmentQuery attachment query param.
     * @return a Page of attachment output dto
     */
    @NonNull
    Page<AttachmentDTO> pageDtosBy(@NonNull Page pageable, AttachmentQuery attachmentQuery);

    /**
     * Uploads file.
     *
     * @param file multipart file must not be null
     * @return attachment info
     * @throws FileOperationException throws when failed to filehandler the file
     */
    @NonNull
    Attachment upload(@NonNull MultipartFile file);

    /**
     * Removes attachment permanently.
     *
     * @param id attachment id must not be null
     * @return attachment detail deleted
     */
    @NonNull
    Attachment removePermanently(@NonNull Integer id);

    /**
     * Removes attachment permanently in batch.
     *
     * @param ids attachment ids must not be null
     * @return attachment detail list deleted
     */
    @NonNull
    List<Attachment> removePermanently(@NonNull Collection<Integer> ids);

    /**
     * Converts to attachment output dto.
     *
     * @param attachment attachment must not be null
     * @return an attachment output dto
     */
    @NonNull
    AttachmentDTO convertToDto(@NonNull Attachment attachment);

    /**
     * List all media type.
     *
     * @return list of media type
     */
    List<String> listAllMediaType();

    /**
     * List all type.
     *
     * @return list of type.
     */
    List<AttachmentType> listAllType();
}
