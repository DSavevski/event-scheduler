package com.sorsix.eventscheduler.service;

import com.sorsix.eventscheduler.domain.PasswordResetToken;
import com.sorsix.eventscheduler.domain.Picture;
import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.domain.VerificationToken;
import com.sorsix.eventscheduler.domain.dto.ResetForgottenPasswordDto;
import com.sorsix.eventscheduler.domain.enums.Provider;
import com.sorsix.eventscheduler.domain.enums.Role;
import com.sorsix.eventscheduler.repository.PasswordTokenRepository;
import com.sorsix.eventscheduler.repository.VerificationTokenRepository;
import com.sorsix.eventscheduler.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.UUID;


@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private VerificationTokenRepository verificationTokenRepository;
    private PasswordTokenRepository passwordTokenRepository;
    private PictureService pictureService;
    private JavaMailSender mailSender;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, VerificationTokenRepository verificationTokenRepository, PasswordTokenRepository passwordTokenRepository, PictureService pictureService, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordTokenRepository = passwordTokenRepository;
        this.pictureService = pictureService;
        this.mailSender = mailSender;
    }


    public User findUserById(Long id) {
        return userRepository.findOne(id);
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

    public User getUserWithPrincipal(Principal principal) {

        if (principal != null) return userRepository.findByUsername(principal.getName());
        else return null;
    }

    public boolean checkForDuplicateUsername(String username) {
        return userRepository.findByUsername(username) != null;
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }


    public User createUser(User user) {
        logger.info("Saving user [{}]", user);
        try {
            user.setRole(Role.USER);
            user.setProvider(Provider.LOCAL);
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            logger.warn("Duplicate username [{}]", user.getUsername());
            return null;
        }

    }

    public User uploadImage(Long id, byte[] data, String contentType, Long size, String name) {

        User user = findUserById(id);

        Picture pictureToSave = new Picture();
        pictureToSave.data = data;
        pictureToSave.contentType = contentType;
        pictureToSave.size = size;
        pictureToSave.fileName = name;
        pictureService.savePicture(pictureToSave);

        Picture oldPicture = user.getPicture();
        if (oldPicture != null) {
            user.setPicture(null);
            pictureService.deletePicture(oldPicture.getId());
        }

        user.setPicture(pictureToSave);
        return updateUser(user);
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
        LocalDateTime now = LocalDateTime.now();

        if (passwordResetToken == null) {
            return false;
        } else if (passwordResetToken.getExpiryDate().isBefore(now)) {
            return false;
        } else {
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

    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        verificationTokenRepository.save(myToken);
    }

    public VerificationToken getVerificationToken(String token) {
        return verificationTokenRepository.findByToken(token);
    }

    public String confirmRegistration(String token) {

        VerificationToken verificationToken = getVerificationToken(token);

        if (verificationToken == null) {
            return "Invalid token";
        }

        User user = verificationToken.getUser();
        LocalDateTime now = LocalDateTime.now();
        if ((verificationToken.getExpiryDate().isBefore(now))) {
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
