package com.coms309.duality.repository;

import com.coms309.duality.model.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    // Find reactions for a specific activity creator
    //List<Reaction> findByActivityCreatorId(long activityCreatorId);

    // Find reactions for a specific activity
    //List<Reaction> findByActivityId(long activityId);


}
