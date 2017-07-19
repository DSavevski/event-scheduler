package com.sorsix.eventscheduler.domain;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Blob;

/**
 * Created by Dragan on 7/18/17.
 */
@Entity
@Table(name = "pictures")
public class Picture extends BaseEntity {

    public Blob data;

    public String fileName;

    public String contentType;

    public int size;
}
