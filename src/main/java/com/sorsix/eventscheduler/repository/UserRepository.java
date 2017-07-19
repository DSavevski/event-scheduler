package com.sorsix.eventscheduler.repository;

import com.sorsix.eventscheduler.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Dragan on 7/18/17.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    @Override
    List<User> findAll();
}
