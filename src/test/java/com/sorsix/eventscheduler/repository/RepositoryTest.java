package com.sorsix.eventscheduler.repository;

import com.sorsix.eventscheduler.domain.Event;
import com.sorsix.eventscheduler.domain.Place;
import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.service.EventService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.Assert;

import java.time.LocalDateTime;

/**
 * Created by Dragan on 7/18/17.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class RepositoryTest {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private EventService eventService;

    @Test
    public void testCreateUser() {
        User user = new User("Dsavevski", "admin", "Dragan", "Savevski");
        User savedUser = userRepository.save(user);
        Assert.assertNotNull(savedUser);
    }

    @Test
    public void testCreatePlace() {
        Place place = new Place("Le petit", "Skopje",
                "Caffe bar");
        placeRepository.save(place);
        place = new Place("Bonimi", "Skopje",
                "Lounge bar");
        placeRepository.save(place);
        place = new Place("Jazz in", "Ohrid",
                "Caffe bar");
        placeRepository.save(place);
    }

    @Test
    public void testCreateEvent() {
        User user = userRepository.findByUsername("Dsavevski");
        Place place = placeRepository.findByName("Lake awake");
        Event event = new Event("The Fest 2", LocalDateTime.now().plusDays(1L),
                LocalDateTime.now().plusDays(2L),"Test desciption",
                user, null,place);
        Event savedEvent = eventRepository.save(event);

        Assert.assertNotNull(savedEvent);

        System.out.println(savedEvent.getStartDate());
        System.out.println(savedEvent.getEndDate());
    }

    @Test
    public void createEvent() {
        User user = userRepository.findOne(4L);
        Place place = placeRepository.findOne(1L);
        Event event = new Event("Friday night",LocalDateTime.now().plusDays(6L),
                LocalDateTime.now().plusDays(7L),"Friday night with some local DJ's",
                user,null, place);
        eventRepository.save(event);
    }

    @Test
    public void deleteEvent(){
        eventService.deleteEvent(1L);
    }

    @Test
    public void updateEvent(){
        Event event = eventRepository.findOne(2L);
        event.setName("Saturday night");
        event.setDescription("Saturday night with DJ Slave");
        eventService.updateEvent(event);
    }
    @Test
    public void testDelete() {

    }
}
