package com.sorsix.eventscheduler.api;

import com.sorsix.eventscheduler.domain.Event;
import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.repository.EventRepository;
import com.sorsix.eventscheduler.repository.UserRepository;
import com.sun.deploy.net.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Dragan on 7/18/17.
 */
@RestController
@RequestMapping(value="/api")
public class UserController {

    private UserRepository userRepository;
    private EventRepository eventRepository;

    public UserController(UserRepository userRepository, EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }



    @GetMapping(value="/getUser")
    public User getUser(){
        return userRepository.findByUsername("Dsavevski");
    }


}
