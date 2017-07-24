package com.sorsix.eventscheduler.api;


import com.sorsix.eventscheduler.domain.Event;
import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.service.EventService;
import com.sorsix.eventscheduler.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/public")
public class PublicController {

    private UserService userService;
    private EventService eventService;

    public PublicController(UserService userService, EventService eventService) {
        this.userService = userService;
        this.eventService = eventService;
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
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

}
