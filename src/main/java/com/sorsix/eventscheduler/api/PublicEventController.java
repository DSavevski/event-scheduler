package com.sorsix.eventscheduler.api;


import com.sorsix.eventscheduler.domain.City;
import com.sorsix.eventscheduler.domain.Event;
import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.domain.dto.DisplayEventsDto;
import com.sorsix.eventscheduler.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/public/events")
public class PublicEventController {

    private EventService eventService;
    private PictureService pictureService;
    private CityService cityService;
    private UserService userService;
    private EventAttendanceService attendanceService;

    public PublicEventController(EventService eventService,
                                 PictureService pictureService, CityService cityService, UserService userService, EventAttendanceService attendanceService) {
        this.eventService = eventService;
        this.pictureService = pictureService;
        this.cityService = cityService;
        this.userService = userService;
        this.attendanceService = attendanceService;
    }

    @GetMapping
    public List<DisplayEventsDto> getAllEvents(Principal principal) {
        User loggedUser = userService.getUserWithPrincipal(principal);
        return eventService.getAllEvents()
                .stream().map(event -> {
                    DisplayEventsDto dto = DisplayEventsDto.fromEvent(event);
                    dto.isGoing = attendanceService.getUsersForEvent(dto.eventId).contains(loggedUser) ? true : false;
                    dto.totalGoings = (long) attendanceService.getUsersForEvent(dto.eventId).size();
                    return dto;
                }).collect(Collectors.toList());
    }

    @GetMapping(value = "/{id}/image")
    public void getPicture(@PathVariable Long id,
                           HttpServletResponse response) throws IOException {
        pictureService.getPicture(id, response);
    }

    @GetMapping(value = "/{city}")
    public List<DisplayEventsDto> filterByCity(@PathVariable("city") String city, Principal principal) {
        User loggedUser = userService.getUserWithPrincipal(principal);
        return eventService.findByCityName(city).stream()
                .map(event -> {
                    DisplayEventsDto dto = DisplayEventsDto.fromEvent(event);
                    dto.isGoing = attendanceService.getUsersForEvent(dto.eventId).contains(loggedUser) ? true : false;
                    dto.totalGoings = (long) attendanceService.getUsersForEvent(dto.eventId).size();
                    return dto;
                }).collect(Collectors.toList());
    }

    @GetMapping(value = "/cities")
    public List<City> getAllCities() {
        return cityService.findAllCities();
    }
}
