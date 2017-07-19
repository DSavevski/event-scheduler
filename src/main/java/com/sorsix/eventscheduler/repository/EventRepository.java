package com.sorsix.eventscheduler.repository;

import com.sorsix.eventscheduler.domain.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Dragan on 7/18/17.
 */
@Repository
public interface EventRepository extends CrudRepository<Event, Long> {
    Event findByName(String name);
}
