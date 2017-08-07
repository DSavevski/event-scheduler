package com.sorsix.eventscheduler.repository;

import com.sorsix.eventscheduler.domain.Picture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PictureRepository extends JpaRepository<Picture, Long> {

}
