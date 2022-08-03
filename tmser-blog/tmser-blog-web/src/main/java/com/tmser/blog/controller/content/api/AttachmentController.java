package com.tmser.blog.controller.content.api;

import com.tmser.blog.model.dto.AttachmentDTO;
import com.tmser.blog.model.entity.Attachment;
import com.tmser.blog.model.enums.AttachmentType;
import com.tmser.blog.model.params.AttachmentParam;
import com.tmser.blog.model.params.AttachmentQuery;
import com.tmser.blog.service.AttachmentService;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import com.tmser.model.sort.Sort;
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
@RequestMapping("/api/content/attachments")
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
    public AttachmentDTO getBy(@PathVariable("id") Integer id,String sign) {

        Attachment attachment = attachmentService.getById(id);
        return attachmentService.convertToDto(attachment);
    }
}
