package com.sorsix.eventscheduler.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_attendance")
public class EventAttendance extends BaseEntity implements Serializable{

    @ManyToOne
    private User user;

    @ManyToOne
    private Event event;

    private LocalDateTime date;


    // Getters, Setters
    public EventAttendance() {
        this.date = LocalDateTime.now();
    }

    public EventAttendance(User user, Event event) {
        this.user = user;
        this.event = event;
        this.date = LocalDateTime.now();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EventAttendance{");
        sb.append("user=").append(user);
        sb.append(", event=").append(event);
        sb.append(", date=").append(date);
        sb.append('}');
        return sb.toString();
    }
}
