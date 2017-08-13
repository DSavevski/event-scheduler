package com.sorsix.eventscheduler.service;

import com.sorsix.eventscheduler.domain.City;
import com.sorsix.eventscheduler.domain.Event;
import com.sorsix.eventscheduler.domain.Picture;
import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.domain.dto.EventCreationDto;
import com.sorsix.eventscheduler.repository.CityRepository;
import com.sorsix.eventscheduler.repository.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EventService {

    private EventRepository eventRepository;
    private CityRepository cityRepository;
    private PictureService pictureService;

    public EventService(EventRepository eventRepository, CityRepository cityRepository, PictureService pictureService) {
        this.eventRepository = eventRepository;
        this.cityRepository = cityRepository;
        this.pictureService = pictureService;
    }

    public Event saveEvent(Event event){
        return eventRepository.save(event);
    }
    public Event findEventById(Long id) {
        return eventRepository.findOne(id);
    }

    public List<Event> getAllEvents() {
       return eventRepository.findAllByEndDateAfter(LocalDateTime.now(), orderBy());
    }

    public List<Event> getUserEvents(Long Id) {
        return eventRepository.findAllByCreatorId(Id, orderBy());
    }

    private Sort orderBy() {
        return new Sort(Sort.Direction.ASC, "startDate");
    }

    public Event createEvent(EventCreationDto dto, User creator) {

        try {
            City city = cityRepository.findOne(dto.getCityId());

            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime startTime = LocalDateTime.parse(dto.getStartTime(),formatter);
            LocalDateTime endTime = LocalDateTime.parse(dto.getEndTime(),formatter);

            Event event = new Event();
            event.setName(dto.getName());
            event.setDescription(dto.getDescription());
            event.setPlace(dto.getPlace());
            event.setCity(city);
            event.setStartDate(startTime);
            event.setEndDate(endTime);
            event.setCreator(creator);

            return eventRepository.save(event);
        }
        catch (Exception e) {
            return null;
        }
    }

    public Event updateEvent(Long Id, String name, String desc, String place, Long cityId) {
        Event event = findEventById(Id);
        City city = cityRepository.findOne(cityId);

        event.setName(name);
        event.setDescription(desc);
        event.setPlace(place);
        event.setCity(city);

        return eventRepository.save(event);
    }

    public Event uploadImage(Long id, byte [] data, String contentType, Long size, String name){
        Event event = findEventById(id);

        Picture pictureToSave = new Picture();
        pictureToSave.data = data;
        pictureToSave.contentType = contentType;
        pictureToSave.size = size;
        pictureToSave.fileName = name;
        pictureService.savePicture(pictureToSave);

        Picture oldPicture = event.getPicture();
        if(oldPicture != null){
            event.setPicture(null);
            pictureService.deletePicture(oldPicture.getId());
        }
        event.setPicture(pictureToSave);

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

    public List<Event> findByCityName(String cityName) {
        return eventRepository.
                findAllByEndDateAfterAndCity(LocalDateTime.now(), cityName, orderBy());
    }



    public Page<Event> listAllByPage(Pageable pageable){
        return eventRepository.findAll(pageable);

    }
}
