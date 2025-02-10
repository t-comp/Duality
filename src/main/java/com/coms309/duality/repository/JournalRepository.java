package com.coms309.duality.repository;

import com.coms309.duality.model.Journal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface JournalRepository extends JpaRepository<Journal, Long> {

    Journal findByTitle(String title);
    boolean existsByTitle(String title);
    Journal findById(long id);

    List<Journal> getJournalByDate(LocalDate date);
}
