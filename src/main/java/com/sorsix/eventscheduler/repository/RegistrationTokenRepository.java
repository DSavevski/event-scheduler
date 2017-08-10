package com.sorsix.eventscheduler.repository;

import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.domain.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationTokenRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);
}
