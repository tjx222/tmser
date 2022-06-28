package com.tmser.blog.controller.admin.api;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tmser.blog.annotation.DisableOnCondition;
import com.tmser.blog.mail.MailService;
import com.tmser.blog.model.params.MailParam;
import com.tmser.blog.model.support.BaseResponse;

/**
 * Mail controller.
 *
 * @author johnniang
 * @date 2019-05-07
 */
@RestController
@RequestMapping("/api/admin/mails")
public class MailController {

    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping("test")
    @DisableOnCondition
    public BaseResponse<String> testMail(@Valid @RequestBody MailParam mailParam) {
        mailService.sendTextMail(mailParam.getTo(), mailParam.getSubject(), mailParam.getContent());
        return BaseResponse.ok("已发送，请查收。若确认没有收到邮件，请检查服务器日志");
    }

    @PostMapping("test/connection")
    @DisableOnCondition
    public BaseResponse<String> testConnection() {
        mailService.testConnection();
        return BaseResponse.ok("您和邮箱服务器的连接通畅");
    }

}
