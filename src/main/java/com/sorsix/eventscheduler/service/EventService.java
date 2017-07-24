package com.sorsix.eventscheduler.service;

import com.sorsix.eventscheduler.domain.Event;
import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.repository.EventRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class EventService {

    private EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getUserEvents(Long Id) {
        return eventRepository.findAllByCreatorId(Id, orderBy());
    }

    private Sort orderBy() {
        return new Sort(Sort.Direction.ASC, "startDate");
    }

    public Event createEvent(Map<String, String> eventData, User creator) {
        try {
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
            return eventRepository.save(event);
        }catch (Exception ex){
            return null;
        }
    }

    public Event updateEvent(Event event) {
        Event toUpdate = eventRepository.findOne(event.getId());
        toUpdate.copy(event);
        return eventRepository.save(toUpdate);
    }

    public void deleteEvent(Long Id) {
        eventRepository.delete(Id);
    }

    private LocalDateTime convertStringToDate(String date, String startTime, String endTime, int i) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String dates[] = date.split("-");
        String start[] = startTime.split("T");
        String end[] = endTime.split("T");

        String dateString = "";

        if (i == 0) {
            dateString = dates[0] + " " + start[1].substring(0, 8);
        } else {
            dateString = dates[1] + " " + end[1].substring(0, 8);
        }

        LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
        return dateTime;

    }
}
