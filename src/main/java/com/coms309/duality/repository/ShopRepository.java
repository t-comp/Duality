package com.coms309.duality.repository;

import com.coms309.duality.model.Item;
import com.coms309.duality.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    //Item findItemById(Long id);

}
