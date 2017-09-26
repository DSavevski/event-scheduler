package com.sorsix.eventscheduler.service;

import com.sorsix.eventscheduler.domain.Event;
import com.sorsix.eventscheduler.domain.EventAttendance;
import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.repository.EventAttendanceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventAttendanceService {

    private EventAttendanceRepository attendanceRepository;


    public EventAttendanceService(EventAttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public List<User> getUsersForEvent(Long eventId) {
        return attendanceRepository.getUsersForEvent(eventId);
    }

    public List<Event> getEventsForUser(Long userId){
        return attendanceRepository.getEventsForUser(userId);
    }

    public EventAttendance save(EventAttendance attendance) {
        return attendanceRepository.save(attendance);
    }

    public EventAttendance findByUserIdAndEventId(Long userId, Long eventId) {
        return attendanceRepository.findByUserIdAndEventId(userId, eventId);
    }

    public void delete(EventAttendance attendance){
        attendanceRepository.delete(attendance);
    }
}
