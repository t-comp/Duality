package com.coms309.duality.repository;

import com.coms309.duality.model.Payment;
import com.coms309.duality.model.Person;
import com.coms309.duality.model.UserItems;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserItemRepository extends JpaRepository<UserItems, Long> {

    List<UserItems> findByPerson(Person person);
}
