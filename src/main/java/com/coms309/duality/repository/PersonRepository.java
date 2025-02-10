package com.coms309.duality.repository;

import com.coms309.duality.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Person findById(long userId);


    Person findByEmail(String email);
    Person findByUsername(String username);
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);

    //public Person findByResetPasswordToken(String resetPasswordToken);


}


