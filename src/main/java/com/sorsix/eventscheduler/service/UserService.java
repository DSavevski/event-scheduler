package com.sorsix.eventscheduler.service;

import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.repository.UserRepository;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;


@Service
public class UserService {

    static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent authenticationSuccessEvent){
        try {
            OAuth2Authentication authentication = (OAuth2Authentication) authenticationSuccessEvent.getAuthentication();
            logger.info("Authentication success [{}]", authentication);
            User user = userRepository.findByUsername(authentication.getName());
            if (user == null) {
                User newUser = new User(authentication.getName());
                logger.info("Creating new user [{}]", newUser);
                userRepository.save(newUser);
            }
        }catch (Exception ex){
            return;
        }
    }

    public User createUser(User user) {
        logger.info("Saving user [{}]", user);
        try {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            logger.info("Encrypted password [{}]", user.getPassword());
            User savedUser = userRepository.save(user);
            return savedUser;
        } catch (DataIntegrityViolationException ex) {
            logger.warn("Duplicate username [{}]", user.getUsername());
            return null;
        }

    }

    public User getUserWithPrincipal(Principal principal){
        if(principal != null)
            return userRepository.findByUsername(principal.getName());
        else
            return null;
    }

    public boolean checkForDuplicateUsername(String username) {
        return userRepository.findByUsername(username) != null;
    }

    public User findByUserName(String username){
        return userRepository.findByUsername(username);
    }

    public User updateUser(User user){
        return userRepository.save(user);
    }

}
