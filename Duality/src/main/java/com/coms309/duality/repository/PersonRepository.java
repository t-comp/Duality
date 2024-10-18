package com.coms309.duality.repository;

import com.coms309.duality.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PersonRepository extends JpaRepository<Person, Long> {
    Person findById(long id);
    Person findByEmail(String email);
    Person findByUsername(String username);
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);

    //public Person findByResetPasswordToken(String resetPasswordToken);


}


