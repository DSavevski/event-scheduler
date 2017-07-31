package com.sorsix.eventscheduler.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Dragan on 7/18/17.
 */
@Table
@Entity(name = "cities")
public class City extends BaseEntity {

    private String name;

    private String description;

    public City() {
    }

    public City(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
