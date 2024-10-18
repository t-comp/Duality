package com.coms309.duality.repository;

import com.coms309.duality.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByActivityName(String activityName);
    List<Activity> findByCategory(String category);

}

