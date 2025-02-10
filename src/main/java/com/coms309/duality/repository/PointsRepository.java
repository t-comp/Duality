package com.coms309.duality.repository;
import com.coms309.duality.model.Points;
import com.coms309.duality.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointsRepository extends JpaRepository<Points, Long> {
    List<Points> findByPersonOrderByDateDesc(Person p);
    List<Points> findByPerson(Person p);

}
