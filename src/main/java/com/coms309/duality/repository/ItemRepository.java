package com.coms309.duality.repository;

import com.coms309.duality.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByType(String type);
    //Optional<Item> findById(Long id);
    List<Item> findByItemName(String itemName);

    Item findItemById(Long itemId);
}


