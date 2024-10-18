package com.coms309.duality.repository;

import com.coms309.duality.model.Journal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JournalRepository extends JpaRepository<Journal, Long> {

    Journal findByTitle(String title);
    boolean existsByTitle(String title);
    Journal findById(long id);
    //boolean random(String prompt);

//    Optional<Journal> findByPromptName(String promptName);
}
