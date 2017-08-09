package com.sorsix.eventscheduler.service;

import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.domain.enums.Provider;
import com.sorsix.eventscheduler.domain.enums.Role;
import com.sorsix.eventscheduler.repository.UserRepository;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.Map;


@Service
public class UserService implements UserDetailsService{

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User findUserById(Long id) {
        return userRepository.findOne(id);
    }

    public User createUser(User user) {
        logger.info("Saving user [{}]", user);
        try {
            user.setRole(Role.USER);
            user.setProvider(Provider.LOCAL);
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            logger.info("Encrypted password [{}]", user.getPassword());
            User savedUser = userRepository.save(user);
            return savedUser;
        } catch (DataIntegrityViolationException ex) {
            logger.warn("Duplicate username [{}]", user.getUsername());
            return null;
        }

    }

    public User getUserWithPrincipal(Principal principal) {

        if (principal != null) {
            return userRepository.findByUsername(principal.getName());
        } else {
            return null;
        }


    }

    public boolean checkForDuplicateUsername(String username) {
        return userRepository.findByUsername(username) != null;
    }

    public User findByUserName(String username) {
        return userRepository.findByUsername(username);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public boolean resetPassword(String oldPassword,
                                 String newPassword,
                                 Principal principal) {

        User user = userRepository.findByUsername(principal.getName());
        if (bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Load user by username [{}]", username);
        User user = userRepository.findByUsername(username);

        if (user != null) {
            return user;
        } else {
            throw new UsernameNotFoundException(String.format("Username [%s] not found", username));
        }
    }
}
