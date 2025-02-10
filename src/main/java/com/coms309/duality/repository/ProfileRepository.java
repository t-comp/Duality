package com.coms309.duality.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.coms309.duality.model.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Profile findById(long id);
}
