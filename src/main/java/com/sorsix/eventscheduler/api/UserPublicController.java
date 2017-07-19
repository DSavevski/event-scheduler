package com.sorsix.eventscheduler.api;


import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/public")
public class UserPublicController {

    private UserService userService;

    public UserPublicController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/register")
    public HttpStatus createUser(@RequestBody User user) {
        System.out.println("=============USER:   " + user);
        if (userService.createUser(user) != null) {
            return HttpStatus.OK;
        } else {
            return HttpStatus.BAD_REQUEST;
        }
    }

    @GetMapping(value = "/duplicate/{username}")
    public String checkForDuplicateUsername(@PathVariable String username) {
        if (userService.checkForDuplicateUsername(username)) {
            return null;
        } else {
            return username;
        }
    }
}
