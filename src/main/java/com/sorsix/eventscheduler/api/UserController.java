package com.sorsix.eventscheduler.api;

import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.security.Principal;
import java.util.Map;

/**
 * Created by Dragan on 7/18/17.
 */

@RestController
@RequestMapping(value = "/api/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public User user(Principal principal) {
        return userService.getUserWithPrincipal(principal);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public User updateUser(@RequestBody Map<String, String> firstAndLastName, Principal principal){
        User user = userService.findByUserName(principal.getName());
        user.setFirstName(firstAndLastName.get("firstName"));
        user.setLastName(firstAndLastName.get("lastName"));
        return userService.updateUser(user);
    }

}
