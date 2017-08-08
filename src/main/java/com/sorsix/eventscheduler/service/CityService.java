package com.sorsix.eventscheduler.service;

import com.sorsix.eventscheduler.domain.City;
import com.sorsix.eventscheduler.repository.CityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {

    private CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public List<City> findAllCities(){
        return cityRepository.findAll();
    }

    public City findById(Long id){
        return cityRepository.findOne(id);
    }
}
