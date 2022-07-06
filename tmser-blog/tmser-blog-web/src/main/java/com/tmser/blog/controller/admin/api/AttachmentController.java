package com.tmser.blog.controller.admin.api;

import com.tmser.blog.model.dto.AttachmentDTO;
import com.tmser.blog.model.entity.Attachment;
import com.tmser.blog.model.enums.AttachmentType;
import com.tmser.blog.model.params.AttachmentParam;
import com.tmser.blog.model.params.AttachmentQuery;
import com.tmser.blog.service.AttachmentService;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import com.tmser.spring.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.LinkedList;
import java.util.List;

/**
 * Attachment controller.
 *
 * @author johnniang
 * @date 2019-03-21
 */
@RestController
@RequestMapping("/api/admin/attachments")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @GetMapping
    public Page<AttachmentDTO> pageBy(
            @PageableDefault(sort = "create_time,DESC") PageImpl pageable,
            AttachmentQuery attachmentQuery) {
        return attachmentService.pageDtosBy(pageable, attachmentQuery);
    }

    @GetMapping("{id:\\d+}")

    public AttachmentDTO getBy(@PathVariable("id") Integer id) {
        Attachment attachment = attachmentService.getById(id);
        return attachmentService.convertToDto(attachment);
    }

    @PutMapping("{attachmentId:\\d+}")
    public AttachmentDTO updateBy(@PathVariable("attachmentId") Integer attachmentId,
                                  @RequestBody @Valid AttachmentParam attachmentParam) {
        Attachment attachment = attachmentService.getById(attachmentId);
        attachmentParam.update(attachment);
        return new AttachmentDTO().convertFrom(attachmentService.update(attachment));
    }

    @DeleteMapping("{id:\\d+}")
    public AttachmentDTO deletePermanently(@PathVariable("id") Integer id) {
        return attachmentService.convertToDto(attachmentService.removePermanently(id));
    }

    @DeleteMapping
    public List<Attachment> deletePermanentlyInBatch(@RequestBody List<Integer> ids) {
        return attachmentService.removePermanently(ids);
    }

    @PostMapping("upload")
    public AttachmentDTO uploadAttachment(@RequestPart("file") MultipartFile file) {
        return attachmentService.convertToDto(attachmentService.upload(file));
    }

    @PostMapping(value = "uploads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<AttachmentDTO> uploadAttachments(@RequestPart("files") MultipartFile[] files) {
        List<AttachmentDTO> result = new LinkedList<>();

        for (MultipartFile file : files) {
            // Upload single file
            Attachment attachment = attachmentService.upload(file);
            // Convert and add
            result.add(attachmentService.convertToDto(attachment));
        }

        return result;
    }

    @GetMapping("media_types")
    public List<String> listMediaTypes() {
        return attachmentService.listAllMediaType();
    }

    @GetMapping("types")
    public List<AttachmentType> listTypes() {
        return attachmentService.listAllType();
    }
}
