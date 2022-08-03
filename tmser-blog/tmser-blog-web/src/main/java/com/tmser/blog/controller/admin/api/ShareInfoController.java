package com.tmser.blog.controller.admin.api;

import com.tmser.blog.annotation.DisableOnCondition;
import com.tmser.blog.event.logger.LogEvent;
import com.tmser.blog.model.dto.AttachmentDTO;
import com.tmser.blog.model.dto.ShareInfoDTO;
import com.tmser.blog.model.entity.Attachment;
import com.tmser.blog.model.entity.ShareInfo;
import com.tmser.blog.model.enums.AttachmentType;
import com.tmser.blog.model.enums.LogType;
import com.tmser.blog.model.params.AttachmentParam;
import com.tmser.blog.model.params.AttachmentQuery;
import com.tmser.blog.model.params.OptionParam;
import com.tmser.blog.model.params.ShareInfoParam;
import com.tmser.blog.security.context.SecurityContext;
import com.tmser.blog.security.context.SecurityContextHolder;
import com.tmser.blog.service.AttachmentService;
import com.tmser.blog.service.ShareInfoService;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import com.tmser.model.sort.Sort;
import com.tmser.spring.web.PageableDefault;
import com.tmser.util.Identities;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
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
@RequestMapping("/api/admin/shareInfo")
public class ShareInfoController {

    @Resource
    private ShareInfoService shareInfoService;

    @Resource
    private ApplicationEventPublisher eventPublisher;


    @GetMapping
    public Page<ShareInfoDTO> pageBy(
            @PageableDefault(sort = "create_time,DESC") PageImpl pageable,
            ShareInfoParam attachmentQuery) {
        return shareInfoService.pageDtosBy(pageable, attachmentQuery);
    }

    @GetMapping("{id:\\d+}")
    public ShareInfoDTO getBy(@PathVariable("id") Integer id) {
        ShareInfo attachment = shareInfoService.getById(id);
        return new ShareInfoDTO().convertFrom(attachment);
    }

    @PutMapping("{id:\\d+}")
    public ShareInfoDTO updateBy(@PathVariable("id") Integer shareInfoId,
                                 @RequestBody @Valid ShareInfoParam shareInfoParam) {
        ShareInfo share = shareInfoService.getById(shareInfoId);
        shareInfoParam.update(share);
        shareInfoService.update(share);
        LogEvent logEvent = new LogEvent(this, share.getId().toString(),
                LogType.SHARE_UPDATE, share.getName());
        eventPublisher.publishEvent(logEvent);
        return new ShareInfoDTO().convertFrom(share);
    }

    @DeleteMapping("{id:\\d+}")
    public ShareInfoDTO delete(@PathVariable("id") Integer id) {
        ShareInfoDTO shareInfoDTO = shareInfoService.deleteById(id);
        LogEvent logEvent = new LogEvent(this, shareInfoDTO.getId().toString(),
                LogType.SHARE_DELETE, shareInfoDTO.getName());
        eventPublisher.publishEvent(logEvent);
        return shareInfoDTO;
    }

    @DeleteMapping
    public List<ShareInfoDTO> deleteInBatch(@RequestBody List<Integer> ids) {
        List<ShareInfoDTO> shareInfoDTOS = shareInfoService.deleteByIds(ids);
        shareInfoDTOS.forEach(shareInfoDTO -> {
            LogEvent logEvent = new LogEvent(this, shareInfoDTO.getId().toString(),
                    LogType.SHARE_DELETE, shareInfoDTO.getName());
            eventPublisher.publishEvent(logEvent);
        });
        return shareInfoDTOS;
    }

    @PostMapping
    @DisableOnCondition
    public ShareInfoDTO createBy(@RequestBody @Valid ShareInfoParam shareInfoParam) {
        ShareInfo shareInfo = new ShareInfo();
        shareInfoParam.update(shareInfo);
        SecurityContext context = SecurityContextHolder.getContext();
        shareInfo.setCreateId(context.getAuthentication().getDetail().getUser().getId());
        shareInfo.setSign(Identities.uuid2());
        final ShareInfo share = shareInfoService.create(shareInfo);

        LogEvent logEvent = new LogEvent(this, share.getId().toString(),
                LogType.SHARE_CREATED, share.getName());
        eventPublisher.publishEvent(logEvent);
        return new ShareInfoDTO().convertFrom(share);
    }
}
