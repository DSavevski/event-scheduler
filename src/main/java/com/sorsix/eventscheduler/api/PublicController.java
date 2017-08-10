package com.sorsix.eventscheduler.api;


import com.sorsix.eventscheduler.domain.*;
import com.sorsix.eventscheduler.domain.dto.ResetForgottenPasswordDto;
import com.sorsix.eventscheduler.events.OnRegistrationCompleteEvent;
import com.sorsix.eventscheduler.service.CityService;
import com.sorsix.eventscheduler.service.EventService;
import com.sorsix.eventscheduler.service.PictureService;
import com.sorsix.eventscheduler.service.UserService;
import com.sun.deploy.net.HttpResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/public")
public class PublicController {

    private UserService userService;
    private EventService eventService;
    private PictureService pictureService;
    private CityService cityService;
    private ApplicationEventPublisher eventPublisher;


    public PublicController(UserService userService, EventService eventService, PictureService pictureService, CityService cityService, ApplicationEventPublisher eventPublisher) {
        this.userService = userService;
        this.eventService = eventService;
        this.pictureService = pictureService;
        this.cityService = cityService;
        this.eventPublisher = eventPublisher;
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Long createUser(@RequestBody User user, HttpServletRequest request) {
        User usr = userService.createUser(user);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(usr, getAppUrl()));

        if (usr != null) return usr.getId();
        else
            return null;
    }

    @GetMapping(value = "/registrationConfirm")
    public String confirmRegistration(@RequestParam("token") String token) {
        return userService.confirmRegistration(token);
    }

    @GetMapping(value = "/duplicate/{username}")
    public boolean checkForDuplicateUsername(@PathVariable String username) {
        return userService.checkForDuplicateUsername(username);
    }

    @GetMapping(value = "/check")
    public boolean checkIfUsernameOrEmailExists(@RequestParam("usernameOrEmail") String usernameOrEmail) {
        return userService.findByEmailOrUsername(usernameOrEmail) != null;
    }

    @GetMapping(value = "/forgot/{usernameOrEmail}")
    public boolean forgotPassword(@PathVariable String usernameOrEmail){
        return userService.forgotPasswordMailSending(usernameOrEmail, getAppUrl());
    }

    @PostMapping(value = "/resetForgottenPassword", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean resetForgottenPassword(@RequestBody ResetForgottenPasswordDto dto){
        return userService.resetForgottenPassword(dto);
    }

    @GetMapping(value = "/events", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping(value = "/image/{id}")
    public void getPicture(@PathVariable Long id,
                           HttpServletResponse httpServletResponse) throws IOException {
        Picture picture = pictureService.findById(id);

        if (picture != null) {
            httpServletResponse.setContentType(picture.contentType);
            httpServletResponse.setContentLength((int) picture.size);
            OutputStream outputStream = httpServletResponse.getOutputStream();
            outputStream.write(picture.data);
            outputStream.flush();
            outputStream.close();
        }
    }

    @GetMapping(value = "/cities", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<City> getAllCities() {
        return cityService.findAllCities();
    }

    @GetMapping(value = "/filter/{cityName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Event> filterByCity(@PathVariable String cityName) {
        return eventService.findByCityName(cityName);
    }

    private String getAppUrl() {
        return "http://localhost:4200";
    }
}
