package com.sorsix.eventscheduler.service;

import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


@Service
public class UserService {

    static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        logger.info("Saving user [{}]", user);
        try {
            User savedUser = userRepository.save(user);
            return savedUser;
        } catch (DataIntegrityViolationException ex) {
            logger.warn("Duplicate username [{}]", user.getUsername());
            return null;
        }

    }

    public boolean checkForDuplicateUsername(String username) {
        boolean found = false;
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                found = true;
                return found;
            }
        }
        return found;
    }

}
