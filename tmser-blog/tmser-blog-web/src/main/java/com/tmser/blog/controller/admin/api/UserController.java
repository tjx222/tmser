package com.tmser.blog.controller.admin.api;

import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tmser.blog.annotation.DisableOnCondition;
import com.tmser.blog.cache.lock.CacheLock;
import com.tmser.blog.exception.BadRequestException;
import com.tmser.blog.model.dto.UserDTO;
import com.tmser.blog.model.entity.User;
import com.tmser.blog.model.enums.MFAType;
import com.tmser.blog.model.params.MultiFactorAuthParam;
import com.tmser.blog.model.params.PasswordParam;
import com.tmser.blog.model.params.UserParam;
import com.tmser.blog.model.support.BaseResponse;
import com.tmser.blog.model.support.UpdateCheck;
import com.tmser.blog.model.vo.MultiFactorAuthVO;
import com.tmser.blog.service.UserService;
import com.tmser.blog.utils.HaloUtils;
import com.tmser.blog.utils.TwoFactorAuthUtils;
import com.tmser.blog.utils.ValidationUtils;

/**
 * User controller.
 *
 * @author johnniang
 * @date 2019-03-19
 */
@RestController
@RequestMapping("/api/admin/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("profiles")
    public UserDTO getProfile(User user) {
        return new UserDTO().convertFrom(user);
    }

    @PutMapping("profiles")
    @DisableOnCondition
    public UserDTO updateProfile(@RequestBody UserParam userParam, User user) {
        // Validate the user param
        ValidationUtils.validate(userParam, UpdateCheck.class);

        // Update properties
        userParam.update(user);

        // Update user and convert to dto
        return new UserDTO().convertFrom(userService.update(user));
    }

    @PutMapping("profiles/password")
    @DisableOnCondition
    public BaseResponse<String> updatePassword(@RequestBody @Valid PasswordParam passwordParam,
        User user) {
        userService.updatePassword(passwordParam.getOldPassword(), passwordParam.getNewPassword(),
            user.getId());
        return BaseResponse.ok("密码修改成功");
    }

    @PutMapping("mfa/generate")
    @DisableOnCondition
    public MultiFactorAuthVO generateMFAQrImage(
        @RequestBody MultiFactorAuthParam multiFactorAuthParam, User user) {
        if (MFAType.NONE == user.getMfaType()) {
            if (MFAType.TFA_TOTP == multiFactorAuthParam.getMfaType()) {
                String mfaKey = TwoFactorAuthUtils.generateTFAKey();
                String optAuthUrl =
                    TwoFactorAuthUtils.generateOtpAuthUrl(user.getNickname(), mfaKey);
                String qrImageBase64 = "data:image/png;base64,"
                    + Base64Utils.encodeToString(
                    HaloUtils.generateQrCodeToPng(optAuthUrl, 128, 128));
                return new MultiFactorAuthVO(qrImageBase64, optAuthUrl, mfaKey, MFAType.TFA_TOTP);
            } else {
                throw new BadRequestException("暂不支持的 MFA 认证的方式");
            }
        } else {
            throw new BadRequestException("MFA 认证已启用，无需重复操作");
        }
    }

    @PutMapping("mfa/update")
    @CacheLock(autoDelete = false, prefix = "mfa")
    @DisableOnCondition
    public MultiFactorAuthVO updateMFAuth(
        @RequestBody @Valid MultiFactorAuthParam multiFactorAuthParam, User user) {
        if (StringUtils.isNotBlank(user.getMfaKey())
            && MFAType.useMFA(multiFactorAuthParam.getMfaType())) {
            return new MultiFactorAuthVO(MFAType.TFA_TOTP);
        } else if (StringUtils.isBlank(user.getMfaKey())
            && !MFAType.useMFA(multiFactorAuthParam.getMfaType())) {
            return new MultiFactorAuthVO(MFAType.NONE);
        } else {
            final String mfaKey = StringUtils.isNotBlank(user.getMfaKey()) ? user.getMfaKey() :
                multiFactorAuthParam.getMfaKey();
            TwoFactorAuthUtils.validateTFACode(mfaKey, multiFactorAuthParam.getAuthcode());
        }
        // update MFA key
        User updateUser = userService
            .updateMFA(multiFactorAuthParam.getMfaType(), multiFactorAuthParam.getMfaKey(),
                user.getId());

        return new MultiFactorAuthVO(updateUser.getMfaType());
    }
}
