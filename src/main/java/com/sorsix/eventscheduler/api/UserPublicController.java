package com.sorsix.eventscheduler.api;


import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/public")
public class UserPublicController {

    private UserService userService;

    public UserPublicController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public HttpStatus createUser(@RequestBody User user) {
        if (userService.createUser(user) != null) {
            return HttpStatus.OK;
        } else {
            return HttpStatus.BAD_REQUEST;
        }
    }

    @GetMapping(value = "/duplicate/{username}")
    public Map<String, String> checkForDuplicateUsername(@PathVariable String username) {
        Map<String, String> json = new HashMap<>();
        if (userService.checkForDuplicateUsername(username)) {
            json.put("username", null);
            return json;
        } else {
            json.put("username",username);
            return json;
        }
    }

    /*@PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public User loginUser(@RequestBody User user) {
        return userService.findByUserName(user.getUsername());
    }*/
}
