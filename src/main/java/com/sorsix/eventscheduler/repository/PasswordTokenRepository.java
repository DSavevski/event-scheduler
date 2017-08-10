package com.sorsix.eventscheduler.repository;

import com.sorsix.eventscheduler.domain.PasswordResetToken;
import com.sorsix.eventscheduler.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);

    PasswordResetToken findByUser(User user);
}
