package com.sorsix.eventscheduler.repository;

import com.sorsix.eventscheduler.domain.Place;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Dragan on 7/18/17.
 */
@Repository
public interface PlaceRepository extends CrudRepository<Place, Long> {
    Place findByName(String name);
}
