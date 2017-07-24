package com.sorsix.eventscheduler.api;

import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Created by Dragan on 7/18/17.
 */

@RestController
@RequestMapping(value = "/api")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({ "/user"})
    public User user(Principal principal) {
        return userService.getUserWithPrincipal(principal);
    }

}
