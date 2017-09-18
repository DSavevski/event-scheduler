package com.sorsix.eventscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Created by Dragan on 7/18/17.
 */
@SpringBootApplication
@EnableAsync
public class EventScheduler {
    public static void main(String[] args) {
        SpringApplication.run(EventScheduler.class, args);
    }
}
