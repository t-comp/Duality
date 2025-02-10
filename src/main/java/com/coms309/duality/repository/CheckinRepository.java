package com.coms309.duality.repository;

import com.coms309.duality.model.Checkin;
import com.coms309.duality.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CheckinRepository extends JpaRepository<Checkin, Long> {
    List<Checkin> findByPersonOrderByDateDesc(Person person);
    List<Checkin> findByPersonAndDate(Person person, LocalDate date);
    List<Checkin> findByPersonAndDateAfterOrderByDateDesc(Person person, LocalDate date);

}
