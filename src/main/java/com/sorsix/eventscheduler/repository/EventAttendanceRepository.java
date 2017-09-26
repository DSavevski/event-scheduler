package com.sorsix.eventscheduler.repository;

import com.sorsix.eventscheduler.domain.Event;
import com.sorsix.eventscheduler.domain.EventAttendance;
import com.sorsix.eventscheduler.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventAttendanceRepository extends JpaRepository<EventAttendance, Long>{

    @Query(value = "select ea.event from EventAttendance ea where ea.user.id =:userId")
    List<Event> getEventsForUser(@Param("userId")Long userId);

    @Query(value = "select ea.user from EventAttendance ea where ea.event.id =:eventId")
    List<User> getUsersForEvent(@Param("eventId") Long eventId);

    EventAttendance findByUserIdAndEventId(Long userId, Long eventId);

    List<EventAttendance> findAllByUserIdAndDateBetween(Long userId, LocalDateTime start, LocalDateTime end);

}
