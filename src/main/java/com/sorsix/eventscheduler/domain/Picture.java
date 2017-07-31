package com.sorsix.eventscheduler.domain;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Blob;
import java.util.Arrays;

/**
 * Created by Dragan on 7/18/17.
 */
@Entity
@Table(name = "pictures")
public class Picture extends BaseEntity {

    public byte[] data;

    public String fileName;

    public String contentType;

    public long size;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Picture{");
        sb.append("data=").append(Arrays.toString(data));
        sb.append(", fileName='").append(fileName).append('\'');
        sb.append(", contentType='").append(contentType).append('\'');
        sb.append(", size=").append(size);
        sb.append('}');
        return sb.toString();
    }
}
