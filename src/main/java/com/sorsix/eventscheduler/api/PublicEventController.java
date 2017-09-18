package com.sorsix.eventscheduler.api;


import com.sorsix.eventscheduler.domain.City;
import com.sorsix.eventscheduler.domain.Event;
import com.sorsix.eventscheduler.service.CityService;
import com.sorsix.eventscheduler.service.EventService;
import com.sorsix.eventscheduler.service.PictureService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/api/public/events")
public class PublicEventController {

    private EventService eventService;
    private PictureService pictureService;
    private CityService cityService;


    public PublicEventController(EventService eventService,
                                 PictureService pictureService, CityService cityService) {
        this.eventService = eventService;
        this.pictureService = pictureService;
        this.cityService = cityService;
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping(value = "/{id}/image")
    public void getPicture(@PathVariable Long id,
                           HttpServletResponse response) throws IOException {
        pictureService.getPicture(id, response);
    }

    @GetMapping(value = "/{city}")
    public List<Event> filterByCity(@PathVariable("city") String city) {
        return eventService.findByCityName(city);
    }

    @GetMapping(value = "/cities")
    public List<City> getAllCities() {
        return cityService.findAllCities();
    }
}
