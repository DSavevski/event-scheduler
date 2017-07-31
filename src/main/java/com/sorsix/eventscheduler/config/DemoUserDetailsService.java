package com.sorsix.eventscheduler.config;

import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class DemoUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(DemoUserDetailsService.class);

    private UserRepository userRepository;

    public DemoUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Load user by username [{}]", username);
        User user = userRepository.findByUsername(username);

        if (user != null) {
            //return new User(username, user.getPassword());
            return user;
        } else {
            throw new UsernameNotFoundException(String.format("Username [%s] not found", username));
        }
    }
}
