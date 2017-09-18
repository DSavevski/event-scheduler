package com.sorsix.eventscheduler.events;

import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.service.UserService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private UserService userService;
    private MessageSource messages;
    private JavaMailSender mailSender;

    public RegistrationListener(UserService userService, MessageSource messages, JavaMailSender mailSender) {
        this.userService = userService;
        this.messages = messages;
        this.mailSender = mailSender;
    }

    @Override
    @Async
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user, token);

        String recipientAddress = user.getEmail();
        String subject = "Registration Confirmation";
        String confirmationUrl = event.getAppUrl() + "/registration?token=" + token;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText("Click the following link in order to activate your account: \n" + confirmationUrl);
        mailSender.send(email);
    }
}
