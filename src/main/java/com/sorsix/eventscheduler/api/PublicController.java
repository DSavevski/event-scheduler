package com.sorsix.eventscheduler.api;


import com.sorsix.eventscheduler.domain.City;
import com.sorsix.eventscheduler.domain.Event;
import com.sorsix.eventscheduler.domain.Picture;
import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.events.OnRegistrationCompleteEvent;
import com.sorsix.eventscheduler.service.CityService;
import com.sorsix.eventscheduler.service.EventService;
import com.sorsix.eventscheduler.service.PictureService;
import com.sorsix.eventscheduler.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
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

        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(usr,
                request.getLocale(), getAppUrl()));
        if (usr != null) {
            return usr.getId();
        } else {
            return null;
        }
    }

    @GetMapping(value = "/duplicate/{username}")
    public Map<String, String> checkForDuplicateUsername(@PathVariable String username) {
        Map<String, String> json = new HashMap<>();
        if (userService.checkForDuplicateUsername(username)) {
            json.put("username", null);
            return json;
        } else {
            json.put("username", username);
            return json;
        }
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
        }
    }

    @GetMapping(value = "/cities", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<City> getAllCities() {
        return cityService.findAllCities();
    }

    @GetMapping(value = "/filter/{cityName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Event> filterByCity(@PathVariable String cityName){
        return eventService.findByCityName(cityName);
    }

    private String getAppUrl() {
        return "http://localhost:4200/";
    }
}
