package com.sorsix.eventscheduler.config;

import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private UserRepository userRepository;

    public LoginFailureHandler() {
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        User user = userRepository.findByUsername(
                request.getParameter("username")
        );

        if(user == null) throw new UsernameNotFoundException("Username was not found");

    }
}
