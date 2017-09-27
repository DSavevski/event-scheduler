package com.sorsix.eventscheduler.tasks;

import com.sorsix.eventscheduler.repository.PasswordTokenRepository;
import com.sorsix.eventscheduler.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@Transactional
public class TokensPurgeTask {

    @Autowired
    VerificationTokenRepository tokenRepository;

    @Autowired
    PasswordTokenRepository passwordTokenRepository;

    @Scheduled(cron = "0 * 0 * * ?")
    public void purgeExpired() {
        LocalDateTime now = LocalDateTime.now();

        passwordTokenRepository.deleteAllExpiredSince(now);
        tokenRepository.deleteAllExpiredSince(now);
    }
}
