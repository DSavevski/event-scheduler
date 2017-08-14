package com.sorsix.eventscheduler.repository;

import com.sorsix.eventscheduler.domain.Event;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Dragan on 7/18/17.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAll(Sort sort);

    Event findByName(String name);

    List<Event> findAllByCityName(String cityName, Sort sort);

    List<Event> findAllByCreatorId(Long Id, Sort sort);

    List<Event> findAllByEndDateAfterAndCity(LocalDateTime end, String city, Sort sort);

    List<Event> findAllByEndDateAfter(LocalDateTime end, Sort sort);

}
