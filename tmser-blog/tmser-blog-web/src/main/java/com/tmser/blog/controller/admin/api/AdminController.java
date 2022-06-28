package com.tmser.blog.controller.admin.api;

import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.tmser.blog.annotation.DisableOnCondition;
import com.tmser.blog.cache.lock.CacheLock;
import com.tmser.blog.model.dto.EnvironmentDTO;
import com.tmser.blog.model.dto.LoginPreCheckDTO;
import com.tmser.blog.model.entity.User;
import com.tmser.blog.model.enums.MFAType;
import com.tmser.blog.model.params.LoginParam;
import com.tmser.blog.model.params.ResetPasswordParam;
import com.tmser.blog.model.params.ResetPasswordSendCodeParam;
import com.tmser.blog.model.properties.PrimaryProperties;
import com.tmser.blog.model.support.BaseResponse;
import com.tmser.blog.security.token.AuthToken;
import com.tmser.blog.service.AdminService;
import com.tmser.blog.service.OptionService;

/**
 * Admin controller.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-03-19
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    private final OptionService optionService;

    public AdminController(AdminService adminService, OptionService optionService) {
        this.adminService = adminService;
        this.optionService = optionService;
    }

    @GetMapping(value = "/is_installed")
    public boolean isInstall() {
        return optionService.getByPropertyOrDefault(PrimaryProperties.IS_INSTALLED, Boolean.class,
            false);
    }

    @PostMapping("login/precheck")
    @CacheLock(autoDelete = false, prefix = "login_precheck")
    public LoginPreCheckDTO authPreCheck(@RequestBody @Valid LoginParam loginParam) {
        final User user = adminService.authenticate(loginParam);
        return new LoginPreCheckDTO(MFAType.useMFA(user.getMfaType()));
    }

    @PostMapping("login")
    @CacheLock(autoDelete = false, prefix = "login_auth")
    public AuthToken auth(@RequestBody @Valid LoginParam loginParam) {
        return adminService.authCodeCheck(loginParam);
    }

    @PostMapping("logout")
    @CacheLock(autoDelete = false)
    public void logout() {
        adminService.clearToken();
    }

    @PostMapping("password/code")
    @CacheLock(autoDelete = false)
    @DisableOnCondition
    public void sendResetCode(@RequestBody @Valid ResetPasswordSendCodeParam param) {
        adminService.sendResetPasswordCode(param);
    }

    @PutMapping("password/reset")
    @CacheLock(autoDelete = false)
    @DisableOnCondition
    public void resetPassword(@RequestBody @Valid ResetPasswordParam param) {
        adminService.resetPasswordByCode(param);
    }

    @PostMapping("refresh/{refreshToken}")
    @CacheLock(autoDelete = false)
    public AuthToken refresh(@PathVariable("refreshToken") String refreshToken) {
        return adminService.refreshToken(refreshToken);
    }

    @GetMapping("environments")
    public EnvironmentDTO getEnvironments() {
        return adminService.getEnvironments();
    }

    @GetMapping(value = "halo/logfile")
    @DisableOnCondition
    public BaseResponse<String> getLogFiles(@RequestParam("lines") Long lines) {
        return BaseResponse.ok(HttpStatus.OK.getReasonPhrase(), adminService.getLogFiles(lines));
    }
}
