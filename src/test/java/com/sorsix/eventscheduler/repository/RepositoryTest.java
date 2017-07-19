package com.sorsix.eventscheduler.repository;

import com.sorsix.eventscheduler.domain.Event;
import com.sorsix.eventscheduler.domain.Place;
import com.sorsix.eventscheduler.domain.User;
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

    @Test
    public void testCreateUser() {
        User user = new User("Dsavevski", "admin", "Dragan", "Savevski");
        User savedUser = userRepository.save(user);
        Assert.assertNotNull(savedUser);
    }

    @Test
    public void testCreatePlace() {
        Place place = new Place("Lake awake", "Dojran",
                "Festival test description");
        Place savedPlace  = placeRepository.save(place);
        Assert.assertNotNull(savedPlace);
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
    public void testRetrieve() {

    }

    @Test
    public void testDelete() {

    }
}
