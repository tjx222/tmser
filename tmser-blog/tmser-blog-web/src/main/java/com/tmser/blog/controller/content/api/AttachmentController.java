package com.tmser.blog.controller.content.api;

import com.tmser.blog.event.logger.VisitLogEvent;
import com.tmser.blog.handler.file.FileHandlers;
import com.tmser.blog.model.dto.AttachmentDTO;
import com.tmser.blog.model.dto.ShareInfoDTO;
import com.tmser.blog.model.entity.Attachment;
import com.tmser.blog.model.params.AttachmentQuery;
import com.tmser.blog.service.AttachmentService;
import com.tmser.blog.service.ShareInfoService;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import com.tmser.spring.web.PageableDefault;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Attachment controller.
 *
 * @author johnniang
 * @date 2019-03-21
 */
@RestController("AttachmentApiController")
@RequestMapping("/api/content/attachments")
public class AttachmentController {

    @Resource
    private  AttachmentService attachmentService;

    @Resource
    private ShareInfoService shareInfoService;

    @Resource
    private FileHandlers fileHandlers;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @GetMapping
    public Page<AttachmentDTO> pageBy(
            @PageableDefault(sort = "create_time,DESC") PageImpl pageable,
            AttachmentQuery attachmentQuery,@RequestParam("sign") String sign) {
        Assert.hasText(sign, "没有权限！");
        ShareInfoDTO shareInfo = shareInfoService.getShareBySign(sign);
        Assert.notNull(shareInfo,"签名不存在！");
        return attachmentService.pageDtosBy(pageable, attachmentQuery);
    }

    @GetMapping("{id:\\d+}")
    public AttachmentDTO getBy(@PathVariable("id") Integer id,String sign) {
        Assert.hasText(sign, "没有权限！");
        ShareInfoDTO shareInfo = shareInfoService.getShareBySign(sign);

        Assert.notNull(shareInfo,"签名不存在！");
        //
        Assert.isTrue(shareInfo.getEndTime().after(new Date()),"分享已过期");
        Attachment attachment = attachmentService.getById(id);
        String previewUrl = fileHandlers.preview(attachment);
        attachment.setThumbPath(previewUrl);
        VisitLogEvent logEvent =
                new VisitLogEvent(this, shareInfo.getId(),
                        attachment.getId()
                        , shareInfo.getName(),
                        attachment.getName());
        eventPublisher.publishEvent(logEvent);
        shareInfoService.updateVisitCount(shareInfo.getId());
        return attachmentService.convertToDto(attachment);
    }
}
