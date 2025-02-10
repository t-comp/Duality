package com.coms309.duality.repository;

import com.coms309.duality.model.Person;
import org.springframework.data.repository.CrudRepository;

public interface UserFriendDAO extends CrudRepository<Person, Long> {
    Person findByEmail(String email1);

    //Person findById(long id);
    //List<Person> findAllByEmail(Set<String> emails);
}
