package com.coms309.duality.services;

import com.coms309.duality.model.Item;

public interface ItemService {

    public Iterable<Item> getAllItems();

    public Item getItem(long id);

    public Item saveItem(Item item);
}
