package com.sorsix.eventscheduler.api;

import com.sorsix.eventscheduler.domain.Event;
import com.sorsix.eventscheduler.domain.Picture;
import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.service.EventService;
import com.sorsix.eventscheduler.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController(value = "/api/event/")
public class EventController {

    private EventService eventService;
    private UserService userService;

    public EventController(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Event> getUserEvents(Principal principal) {
        User loggedUser = userService.findByUserName(principal.getName());
        return this.eventService.getUserEvents(loggedUser.getId());
    }

    @PostMapping
    public Long createEvent(@RequestBody Map<String, String> map, Principal principal) {
        User creator = userService.findByUserName(principal.getName());
        Event event = eventService.createEvent(map, creator);

        if (event != null) {
            System.out.println(event);
            return event.getId();
        } else {
            return null;
        }
    }

    @PostMapping(value = "/upload/{id}")
    public void uploadImage(@RequestParam("image") MultipartFile image, @PathVariable Long id) throws IOException {

        Picture pictureToSave = new Picture();
        pictureToSave.data = image.getBytes();
        pictureToSave.contentType = image.getContentType();
        pictureToSave.size = image.getSize();
        pictureToSave.fileName = image.getName();

        System.out.println("===========ID: " + id);
        System.out.println("==========PICture: " + pictureToSave);


    }





}
