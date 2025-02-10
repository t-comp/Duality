package com.coms309.duality.repository;

import com.coms309.duality.model.Person;
import com.coms309.duality.model.Settings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsRepository extends JpaRepository<Settings, Long> {
    Settings findByPerson(Person person);
}
