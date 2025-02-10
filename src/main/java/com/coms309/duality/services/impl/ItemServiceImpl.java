package com.coms309.duality.services.impl;


import com.coms309.duality.model.Item;
import com.coms309.duality.repository.ItemRepository;
import com.coms309.duality.services.ItemService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public Iterable<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Override
    public Item getItem(long id) {
        return itemRepository.findItemById(id);
    }

    @Override
    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }
}
