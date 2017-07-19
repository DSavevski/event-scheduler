package com.sorsix.eventscheduler.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Dragan on 7/18/17.
 */
@Table
@Entity(name = "places")
public class Place extends BaseEntity {

    private String name;

    private String city;

    private String description;

    public Place() {
    }

    public Place(String name, String city, String description) {
        this.name = name;
        this.city = city;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
