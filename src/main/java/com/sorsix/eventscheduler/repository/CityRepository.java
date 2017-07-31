package com.sorsix.eventscheduler.repository;

import com.sorsix.eventscheduler.domain.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Dragan on 7/18/17.
 */
@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    City findByName(String name);

    List<City> findAll();
}
