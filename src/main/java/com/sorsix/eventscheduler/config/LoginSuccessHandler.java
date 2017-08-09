package com.sorsix.eventscheduler.config;

import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.domain.enums.Provider;
import com.sorsix.eventscheduler.domain.enums.Role;
import com.sorsix.eventscheduler.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationHandlers.class);

    @Autowired
    private UserRepository userRepository;

    private Provider provider;
    private Role role;


    public LoginSuccessHandler(Provider provider, Role role) {
        this.provider = provider;
        this.role = role;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        logger.info("Authentication success [{}]", authentication);
        User user = userRepository.findByUsername(authentication.getName());

        if (user == null) {
            Map<String, String> map = (Map<String, String>) ((OAuth2Authentication) authentication).getUserAuthentication().getDetails();
            user = new User(authentication.getName());
            user.setProvider(this.provider);
            user.setRole(this.role);

            if (map.get("name") != null) {
                String[] parts = map.get("name").split(" ");

                if (parts.length > 1) {
                    user.setFirstName(parts[0]);
                    user.setLastName(parts[1]);
                } else {
                    user.setFirstName(map.get("name"));
                }
            }

            logger.info("Creating new user [{}]", user);
            userRepository.save(user);
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }

}
