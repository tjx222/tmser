package com.tmser.blog.controller.content.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tmser.blog.model.dto.UserDTO;
import com.tmser.blog.service.UserService;

/**
 * Content user controller.
 *
 * @author johnniang
 * @date 2019-04-03
 */
@RestController("ApiContentUserController")
@RequestMapping("/api/content/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("profile")
    public UserDTO getProfile() {
        return userService.getCurrentUser().map(user -> (UserDTO) new UserDTO().convertFrom(user))
            .get();
    }
}
