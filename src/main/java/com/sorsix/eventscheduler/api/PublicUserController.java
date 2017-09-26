package com.sorsix.eventscheduler.api;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.domain.dto.ResetForgottenPasswordDto;
import com.sorsix.eventscheduler.events.OnRegistrationCompleteEvent;
import com.sorsix.eventscheduler.service.PayPalService;
import com.sorsix.eventscheduler.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(value = "/api/public/users")
public class PublicUserController {

    private UserService userService;
    private ApplicationEventPublisher eventPublisher;
    private PayPalService payPalService;

    public PublicUserController(UserService userService, ApplicationEventPublisher eventPublisher, PayPalService payPalService) {
        this.userService = userService;
        this.eventPublisher = eventPublisher;
        this.payPalService = payPalService;
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
    public boolean forgetPassword(@PathVariable("username_email") String usernameOrEmail) {
        // Send mail to the specified username/email
        return userService.forgotPasswordMailSending(usernameOrEmail, getAppUrl());
    }

    @PostMapping(value = "/reset/forget_password")
    public boolean resetForgottenPassword(@RequestBody ResetForgottenPasswordDto dto) {
        return userService.resetForgottenPassword(dto);
    }

    //should be movde to UserController
    @GetMapping(value = "/donate")
    public String donate() {
        Payment payment = payPalService.createPayment();
        List<Links> links = payment.getLinks().stream()
                .filter(element -> {
                    if (element.getRel().equalsIgnoreCase("approval_url"))
                        return true;
                    return false;
                }).collect(Collectors.toList());
        return links.get(0).toJSON();
    }

    @GetMapping(value = "/process")
    public Payment process(@RequestParam String paymentId, @RequestParam String token, @RequestParam String PayerID) {
       return payPalService.executePayment(paymentId, PayerID);
    }
    //should be movde to UserController


    //HELPERS
    private String getAppUrl() {
        return "http://localhost:4200";
    }

}
