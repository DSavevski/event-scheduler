package com.sorsix.eventscheduler.api;

import com.sorsix.eventscheduler.domain.Event;
import com.sorsix.eventscheduler.domain.EventAttendance;
import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.domain.dto.EventCreationDto;
import com.sorsix.eventscheduler.service.EventAttendanceService;
import com.sorsix.eventscheduler.service.EventService;
import com.sorsix.eventscheduler.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private EventService eventService;
    private UserService userService;
    private EventAttendanceService attendanceService;

    public EventController(EventService eventService, UserService userService, EventAttendanceService attendanceService) {
        this.eventService = eventService;
        this.userService = userService;
        this.attendanceService = attendanceService;
    }

    @DeleteMapping(value = "/{id}")
    public String deleteEvent(@PathVariable Long id) {
        return eventService.deleteEvent(id);
    }

    @GetMapping()
    public List<Event> getUserEvents(Principal principal) {
        User loggedUser = userService.findByUserName(principal.getName());
        return this.eventService.getUserEvents(loggedUser.getId());
    }

    @GetMapping(value = "/{id}")
    public Event getEvent(@PathVariable Long id) {
        return eventService.findEventById(id);
    }

    @PutMapping()
    public boolean updateEvent(@RequestBody Map<String, String> newEventData) {
        Long Id = Long.parseLong(newEventData.get("id"));
        String name = newEventData.get("name");
        String desc = newEventData.get("description");
        String place = newEventData.get("place");
        Long cityId = Long.parseLong(newEventData.get("cityId"));

        Event event = eventService.updateEvent(Id, name, desc, place, cityId);
        return event != null;
    }

    @PostMapping()
    public Long createEvent(@RequestBody EventCreationDto dto, Principal principal) {
        User creator = userService.findByUserName(principal.getName());
        Event event = eventService.createEvent(dto, creator);

        if (event != null) return event.getId();
        else return null;
    }

    @PostMapping(value = "/{id}/upload_image")
    public boolean uploadImage(@RequestParam("image") MultipartFile image,
                               @PathVariable Long id) throws IOException {

        byte[] data = image.getBytes();
        String contentType = image.getContentType();
        Long size = image.getSize();
        String fileName = image.getName();

        return eventService.uploadImage(id, data, contentType, size, fileName) != null;
    }

    // To change
    @GetMapping(value = "/{id}/going")
    public String goingToEvent(@PathVariable Long id, Principal principal) {
        Event event = eventService.findEventById(id);
        User loggedUser = userService.findByUserName(principal.getName());

        EventAttendance attendance = new EventAttendance();
        attendance.setEvent(event);
        attendance.setUser(loggedUser);
        attendance.setDate(LocalDateTime.now());

        attendanceService.save(attendance);
        return "Success!";
    }

    @DeleteMapping(value = "/{id}/cancel")
    public void cancelGoing(@PathVariable("id") Long eventId, Principal principal) {
        User user = userService.findByUserName(principal.getName());
        EventAttendance attendance = attendanceService.findByUserIdAndEventId(user.getId(), eventId);
        attendanceService.delete(attendance);
    }

    @GetMapping(value = "/user")
    public List<Event> getEventsWhereUserGoing(Principal principal) {
        User loggedUser = userService.getUserWithPrincipal(principal);
        return attendanceService.getEventsForUser(loggedUser.getId());
    }
}
