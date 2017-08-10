package com.sorsix.eventscheduler.service;

import com.sorsix.eventscheduler.domain.PasswordResetToken;
import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.domain.VerificationToken;
import com.sorsix.eventscheduler.domain.dto.ResetForgottenPasswordDto;
import com.sorsix.eventscheduler.domain.enums.Provider;
import com.sorsix.eventscheduler.domain.enums.Role;
import com.sorsix.eventscheduler.repository.PasswordTokenRepository;
import com.sorsix.eventscheduler.repository.RegistrationTokenRepository;
import com.sorsix.eventscheduler.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.Calendar;
import java.util.UUID;


@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private RegistrationTokenRepository registrationTokenRepository;
    private PasswordTokenRepository passwordTokenRepository;
    private JavaMailSender mailSender;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, RegistrationTokenRepository registrationTokenRepository, PasswordTokenRepository passwordTokenRepository, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.registrationTokenRepository = registrationTokenRepository;
        this.passwordTokenRepository = passwordTokenRepository;
        this.mailSender = mailSender;
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
            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            logger.warn("Duplicate username [{}]", user.getUsername());
            return null;
        }

    }

    public User getUserWithPrincipal(Principal principal) {

        if (principal != null) return userRepository.findByUsername(principal.getName());
        else return null;
    }

    public boolean checkForDuplicateUsername(String username) {
        return userRepository.findByUsername(username) != null;
    }

    public User findByUserName(String username) {
        return userRepository.findByUsername(username);
    }

    public User findByEmailOrUsername(String emailOrUsername) {
        User userWithUsername;
        User userWithEmail;

        userWithEmail = userRepository.findByEmail(emailOrUsername);
        userWithUsername = userRepository.findByUsername(emailOrUsername);

        if (userWithEmail != null) {
            return userWithEmail;
        }
        return userWithUsername;
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public boolean resetPassword(String oldPassword, String newPassword,
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

    public boolean resetForgottenPassword(ResetForgottenPasswordDto dto) {

        PasswordResetToken passwordResetToken = getPasswordResetToken(dto.getToken());
        Calendar cal = Calendar.getInstance();
        if (passwordResetToken == null) {
            return false;
        }
        else if (passwordResetToken.getExpiryDate().getTime() - cal.getTime().getTime() <= 0) {
            return false;
        }
        else {
            User user = passwordResetToken.getUser();
            String encrypted = bCryptPasswordEncoder.encode(dto.getPassword());
            user.setPassword(encrypted);
            return updateUser(user) != null;
        }

    }

    public boolean forgotPasswordMailSending(String usernameOrEmail, String appUrl) {
        User user = findByEmailOrUsername(usernameOrEmail);
        String token = UUID.randomUUID().toString();
        createPasswordResetTokenForUser(user, token);

        String confirmationUrl = appUrl + "/reset-password?token=" + token;
        SimpleMailMessage email = constructEmail("Reset password", confirmationUrl, user);
        mailSender.send(email);

        return true;
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

    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        registrationTokenRepository.save(myToken);
    }

    public VerificationToken getVerificationToken(String token) {
        return registrationTokenRepository.findByToken(token);
    }

    public String confirmRegistration(String token) {

        VerificationToken verificationToken = getVerificationToken(token);

        if (verificationToken == null) {
            return "Invalid token";
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            return "Token expired";
        }

        user.setEnabled(true);
        updateUser(user);
        return "Account activated";
    }

    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordTokenRepository.save(myToken);
    }

    public PasswordResetToken getPasswordResetToken(String token) {
        return passwordTokenRepository.findByToken(token);
    }

    private SimpleMailMessage constructEmail(String subject, String body, User user) {
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        return email;
    }

}
