package com.sorsix.eventscheduler.repository;

import com.sorsix.eventscheduler.domain.City;
import com.sorsix.eventscheduler.domain.Event;
import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.service.EventService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.Assert;

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
    private CityRepository cityRepository;
    @Autowired
    private EventService eventService;

    @Test
    public void testCreateUser() {
        User user = new User("Dsavevski", "admin", "Dragan", "Savevski");
        User savedUser = userRepository.save(user);
        Assert.assertNotNull(savedUser);
    }

    @Test
    public void deleteEvent() {
        eventService.deleteEvent(1L);
    }

    @Test
    public void updateEvent() {
        Event event = eventRepository.findOne(2L);
        event.setName("Saturday night");
        event.setDescription("Saturday night with DJ Slave");
        eventService.updateEvent(event);
    }

    @Test
    public void testDelete() {
        Event event = eventRepository.findOne(4L);
        eventRepository.delete(event.getId());

    }

    @Test
    public void createCities() {
        City city1 = new City("Skopje", null);
        City city2 = new City("Ohrid", null);
        City city3 = new City("Bitola", null);
        City city4 = new City("Stip", null);
        City city5 = new City("Dojran", null);
        City city6 = new City("Tetovo", null);
        City city7 = new City("Prilep", null);
        City city8 = new City("Kumanovo", null);
        City city9 = new City("Strumica", null);
        City city10 = new City("Veles", null);

        cityRepository.save(city1);
        cityRepository.save(city2);
        cityRepository.save(city3);
        cityRepository.save(city4);
        cityRepository.save(city5);
        cityRepository.save(city6);
        cityRepository.save(city7);
        cityRepository.save(city8);
        cityRepository.save(city9);
        cityRepository.save(city10);
    }
}
