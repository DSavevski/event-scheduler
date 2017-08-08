package com.sorsix.eventscheduler.service;

import com.sorsix.eventscheduler.domain.City;
import com.sorsix.eventscheduler.domain.Event;
import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.repository.CityRepository;
import com.sorsix.eventscheduler.repository.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EventService {

    private EventRepository eventRepository;
    private CityRepository cityRepository;

    public EventService(EventRepository eventRepository, CityRepository cityRepository) {
        this.eventRepository = eventRepository;
        this.cityRepository = cityRepository;
    }

    public Event findEventById(Long id) {
        return eventRepository.findOne(id);
    }

    public List<Event> getAllEvents() {
        List<Event> events =eventRepository.findAll(orderBy());


        return filterEvents(events);
    }

    public List<Event> getUserEvents(Long Id) {
        return eventRepository.findAllByCreatorId(Id, orderBy());
    }

    private Sort orderBy() {
        return new Sort(Sort.Direction.ASC, "startDate");
    }

    public Event createEvent(Map<String, String> eventData, User creator) {
        try {
            Long Id = Long.parseLong(eventData.get("cityId"));
            City city = cityRepository.findOne(Id);

            Event event = new Event();
            event.setName(eventData.get("name"));
            event.setDescription(eventData.get("description"));

            LocalDateTime start = convertStringToDate
                    (eventData.get("date"), eventData.get("startTime"), eventData.get("endTime"), 0);
            LocalDateTime end = convertStringToDate
                    (eventData.get("date"), eventData.get("startTime"), eventData.get("endTime"), 1);

            event.setStartDate(start);
            event.setEndDate(end);
            event.setCreator(creator);
            event.setPlace(eventData.get("place"));
            event.setCity(city);

            return eventRepository.save(event);

        } catch (Exception ex) {
            return null;
        }
    }

    public Event updateEvent(Event event) {
        return eventRepository.save(event);
    }

    public String deleteEvent(Long Id) {
        try {
            eventRepository.delete(Id);
            return "Successfully deleted!";
        } catch (Exception ex) {
            return "Event was not deleted!";
        }

    }

    private LocalDateTime convertStringToDate(String date, String startTime, String endTime, int i) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String dates[] = date.split("-");
        String start[] = startTime.split("T");
        String end[] = endTime.split("T");

        String dateString;

        if (i == 0) {
            dateString = dates[0] + " " + start[1].substring(0, 8);
        } else {
            dateString = dates[1] + " " + end[1].substring(0, 8);
        }

        LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
        return dateTime;
    }

    public List<Event> findByCityName(String cityName) {
        List<Event> events = eventRepository.findAllByCityName(cityName, orderBy());
        return filterEvents(events);
    }

    public List<Event> filterEvents(List<Event> events){
        LocalDateTime dateTime= LocalDateTime.now();
        List<Event> filterList = new ArrayList<>();
        for(int i =0;i<events.size(); i++){
            Event tmp = events.get(i);
            if(tmp.getEndDate().isAfter(dateTime)){
                filterList.add(tmp);
            }
        }
        return filterList;

    }

    public Page<Event> listAllByPage(Pageable pageable){
        return eventRepository.findAll(pageable);

    }
}
