package com.coms309.duality.repository;

import com.coms309.duality.model.Checkin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckinRepository extends JpaRepository<Checkin, Long> {

}
