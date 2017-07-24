package com.sorsix.eventscheduler.domain;

import org.apache.tomcat.jni.Local;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dragan on 7/18/17.
 */
@Table
@Entity(name = "events")
public class Event extends BaseEntity {

    private String name;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    private String description;

    @ManyToOne
    private User creator;

    @ManyToMany
    private List<User> attendingUsers = new ArrayList<>();

    @ManyToOne
    private Place place;

    @OneToOne
    private Picture picture;

    public Event() {
    }

    public Event(String name, LocalDateTime startDate, LocalDateTime endDate,
                 String description, User creator, List<User> attendingUsers, Place place) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.creator = creator;
        this.attendingUsers = attendingUsers;
        this.place = place;
    }

    public void copy(Event event){
        this.name = event.name;
        this.startDate = event.startDate;
        this.endDate = event.endDate;
        this.description = event.description;
        this.place = event.place;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public String getDescription() {
        return description;
    }

    public User getCreator() {
        return creator;
    }

    public List<User> getAttendingUsers() {
        return attendingUsers;
    }

    public Place getPlace() {
        return place;
    }

    public Picture getPicture() {
        return picture;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public void setAttendingUsers(List<User> attendingUsers) {
        this.attendingUsers = attendingUsers;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Event{");
        sb.append("name='").append(name).append('\'');
        sb.append(", startDate=").append(startDate);
        sb.append(", endDate=").append(endDate);
        sb.append(", description='").append(description).append('\'');
        sb.append(", creator=").append(creator);
        sb.append('}');
        return sb.toString();
    }
}
