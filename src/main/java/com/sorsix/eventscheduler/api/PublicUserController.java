package com.sorsix.eventscheduler.api;

import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.domain.dto.ResetForgottenPasswordDto;
import com.sorsix.eventscheduler.events.OnRegistrationCompleteEvent;
import com.sorsix.eventscheduler.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/api/public/users")
public class PublicUserController {

    private UserService userService;
    private ApplicationEventPublisher eventPublisher;

    public PublicUserController(UserService userService, ApplicationEventPublisher eventPublisher) {
        this.userService = userService;
        this.eventPublisher = eventPublisher;
    }

    @PostMapping(value = "/register")
    public Long createUser(@RequestBody User user) {
        User usr = userService.createUser(user);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(usr, getAppUrl()));

        if (usr != null) return usr.getId();
        else return null;
    }

    @PutMapping(value = "/register/confirm")
    public String confirmRegistration(@RequestParam("token") String token) {
        return userService.confirmRegistration(token);
    }

    @GetMapping(value = "/exists/register")
    public boolean checkForDuplicateUsername(@RequestParam("username") String username) {
        return userService.checkForDuplicateUsername(username);
    }

    @GetMapping(value = "/exists/forget_password")
    public boolean checkIfUsernameOrEmailExists(@RequestParam("username_email") String usernameOrEmail) {
        return userService.findByEmailOrUsername(usernameOrEmail) != null;
    }

    @PostMapping(value = "/forget_password/{username_email}")
    public boolean forgetPassword(@PathVariable("username_email") String usernameOrEmail){
        // Send mail to the specified username/email
        return userService.forgotPasswordMailSending(usernameOrEmail, getAppUrl());
    }

    @PostMapping(value = "/reset/forget_password")
    public boolean resetForgottenPassword(@RequestBody ResetForgottenPasswordDto dto){
        return userService.resetForgottenPassword(dto);
    }

    //HELPERS
    private String getAppUrl() {
        return "http://localhost:4200";
    }

}
