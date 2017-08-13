package com.sorsix.eventscheduler.api;

import com.sorsix.eventscheduler.domain.Event;
import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.domain.dto.EventCreationDto;
import com.sorsix.eventscheduler.service.EventService;
import com.sorsix.eventscheduler.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public EventController(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    @DeleteMapping(value = "/{eventId}")
    public String deleteEvent(@PathVariable Long eventId) {
        return eventService.deleteEvent(eventId);
    }

    @GetMapping()
    public List<Event> getUserEvents(Principal principal) {
        User loggedUser = userService.findByUserName(principal.getName());
        return this.eventService.getUserEvents(loggedUser.getId());
    }

    @GetMapping(value = "/{id}")
    public Event getEvent(@PathVariable Long id){
        return eventService.findEventById(id);
    }

    @PutMapping()
    public boolean updateEvent(@RequestBody Map<String, String> newEventData){
        Long Id = Long.parseLong(newEventData.get("id"));
        String name = newEventData.get("name");
        String desc = newEventData.get("description");
        String place = newEventData.get("place");
        Long cityId = Long.parseLong(newEventData.get("cityId"));

        Event event = eventService.updateEvent(Id,name,desc,place,cityId);
        return event != null;
    }

    @PostMapping()
    public Long createEvent(@RequestBody EventCreationDto dto, Principal principal) {
        User creator = userService.findByUserName(principal.getName());
        Event event = eventService.createEvent(dto, creator);

        if (event != null) return event.getId();
        else return null;
    }

    @PostMapping(value = "/upload/{id}")
    public void uploadImage(@RequestParam("image") MultipartFile image,
                            @PathVariable Long id) throws IOException {

        byte [] data = image.getBytes();
        String contentType = image.getContentType();
        Long size = image.getSize();
        String fileName = image.getName();

        eventService.uploadImage(id, data, contentType, size, fileName);
    }

    @GetMapping(value = "/going/{eventId}")
    public String goingToEvent(@PathVariable Long eventId, Principal principal) {
        Event event = eventService.findEventById(eventId);
        User loggedUser = userService.findByUserName(principal.getName());

        event.addToAttendingUsers(loggedUser);

        eventService.saveEvent(event);

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
        eventService.saveEvent(event);
    }

    @GetMapping(value = "/page")
    public Page<Event> listByPage(Pageable pageable){
        return eventService.listAllByPage(pageable);
    }

}
