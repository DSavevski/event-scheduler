package com.sorsix.eventscheduler.api;

import com.sorsix.eventscheduler.domain.City;
import com.sorsix.eventscheduler.domain.Event;
import com.sorsix.eventscheduler.domain.Picture;
import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.service.CityService;
import com.sorsix.eventscheduler.service.EventService;
import com.sorsix.eventscheduler.service.PictureService;
import com.sorsix.eventscheduler.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/event")
public class EventController {

    private EventService eventService;
    private UserService userService;
    private PictureService pictureService;
    private CityService cityService;

    public EventController(EventService eventService, UserService userService, PictureService pictureService, CityService cityService) {
        this.eventService = eventService;
        this.userService = userService;
        this.pictureService = pictureService;
        this.cityService = cityService;
    }

    @DeleteMapping(value = "/{eventId}")
    public String deleteEvent(@PathVariable Long eventId) {
        return eventService.deleteEvent(eventId);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Event> getUserEvents(Principal principal) {
        User loggedUser = userService.findByUserName(principal.getName());
        return this.eventService.getUserEvents(loggedUser.getId());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Event getEvent(@PathVariable Long id){
        Event event = eventService.findEventById(id);
        return event;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Event updateEvent(@RequestBody Map<String, String> newEventData){
        Long Id = Long.parseLong(newEventData.get("id"));
        Event event = eventService.findEventById(Id);

        event.setName(newEventData.get("name"));
        event.setDescription(newEventData.get("description"));
        event.setPlace(newEventData.get("place"));

        Long cityId = Long.parseLong(newEventData.get("cityId"));
        City city = cityService.findById(cityId);

        event.setCity(city);
        return eventService.updateEvent(event);
    }

    @PostMapping
    public Long createEvent(@RequestBody Map<String, String> map, Principal principal) {
        User creator = userService.findByUserName(principal.getName());
        Event event = eventService.createEvent(map, creator);

        if (event != null) {
            return event.getId();
        } else {
            return null;
        }
    }

    @PostMapping(value = "/upload/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadImage(@RequestParam("image") MultipartFile image,
                            @PathVariable Long id) throws IOException {

        Event event = eventService.findEventById(id);

        Picture pictureToSave = new Picture();
        pictureToSave.data = image.getBytes();
        pictureToSave.contentType = image.getContentType();
        pictureToSave.size = image.getSize();
        pictureToSave.fileName = image.getName();
        pictureService.savePicture(pictureToSave);

        Picture oldPicture= event.getPicture();
        if(oldPicture != null){
            event.setPicture(null);
            pictureService.deletePicture(oldPicture.getId());
        }
        event.setPicture(pictureToSave);

        eventService.updateEvent(event);
    }

    @GetMapping(value = "/going/{eventId}")
    public String goingToEvent(@PathVariable Long eventId, Principal principal) {
        Event event = eventService.findEventById(eventId);
        User loggedUser = userService.findByUserName(principal.getName());

        event.addToAttendingUsers(loggedUser);

        eventService.updateEvent(event);

        return "Success!";
    }

    @GetMapping(value = "/check/{eventId}")
    public boolean checkIfGoing(@PathVariable Long eventId, Principal principal) {
        Event event = eventService.findEventById(eventId);
        User loggedUser = userService.findByUserName(principal.getName());

        return event.chekIfUserGoing(loggedUser);
    }

    @DeleteMapping(value = "/cancel/{eventId}")
    public void cancelGoing(@PathVariable Long eventId, Principal principal) {
        Event event = eventService.findEventById(eventId);
        User user = userService.findByUserName(principal.getName());
        event.cancelGoing(user);
        eventService.updateEvent(event);
    }

    @GetMapping(value = "/page")
    public Page<Event> listByPage(Pageable pageable){
        return eventService.listAllByPage(pageable);
    }

}
