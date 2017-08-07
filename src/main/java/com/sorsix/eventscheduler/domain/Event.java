package com.sorsix.eventscheduler.domain;

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

    private String place;

    private String description;

    @ManyToOne
    private User creator;

    @ManyToMany
    private List<User> attendingUsers = new ArrayList<>();

    @ManyToOne
    private City city;

    @OneToOne
    private Picture picture;

    public Event() {
    }

    public Event(String name, LocalDateTime startDate, LocalDateTime endDate,
                 String description, User creator, List<User> attendingUsers, String place) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.creator = creator;
        this.attendingUsers = attendingUsers;
        this.place = place;
    }

    public void copy(Event event) {
        this.name = event.name;
        this.startDate = event.startDate;
        this.endDate = event.endDate;
        this.description = event.description;
        this.city = event.city;
    }

    public void addToAttendingUsers(User user) {
        attendingUsers.add(user);
    }

    public boolean chekIfUserGoing(User user) {
        return attendingUsers.contains(user);
    }

    public boolean cancelGoing(User user) {
        return attendingUsers.remove(user);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<User> getAttendingUsers() {
        return attendingUsers;
    }

    public void setAttendingUsers(List<User> attendingUsers) {
        this.attendingUsers = attendingUsers;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Picture getPicture() {
        return picture;
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
