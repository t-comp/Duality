package com.coms309.duality.controllers;


import com.coms309.duality.model.*;
import com.coms309.duality.repository.ItemRepository;
import com.coms309.duality.repository.PersonRepository;
import com.coms309.duality.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.coms309.duality.model.ItemType.CHECKIN_CUSTOM;
import static com.coms309.duality.model.ItemType.PROFILE_CUSTOM;


@RestController
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PaymentController paymentController;




    public boolean isValidCategory(String type) {
        ItemType[] itemTypes = ItemType.values();
        for (ItemType itemType : itemTypes) {
            if (itemType.name().equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }



    private Shop getOrCreateMainShop() {

        List<Shop> shops = shopRepository.findAll();
        if (shops.isEmpty()) {
            Shop newShop = new Shop();
            return shopRepository.save(newShop);
        }
        return shops.getFirst();

    }

    @PutMapping("/admin/newItem")
    public String newItem(@RequestBody Item newItem) {
        if(newItem == null || !isValidCategory(newItem.getType())) {
            return "Not a valid category";
        }


        try{
            switch (ItemType.valueOf(newItem.getType().toUpperCase())){
                case PROFILE_CUSTOM:
                    newItem.setPrice(300);
                    break;
                case CHECKIN_CUSTOM:
                    newItem.setPrice(500);
                    break;
                default:
                    newItem.setPrice(700);
            }

            Shop shop = getOrCreateMainShop();
            newItem.setShop(shop);

            Item savedItem = itemRepository.save(newItem);
            shop.getInventory().add(savedItem);
            shopRepository.save(shop);
            return "New Item Saved";
        } catch (Exception e) {
            return "Error creating new item" + e.getMessage();
        }

    }

    @GetMapping("/items/{itemType}")
    public List<Item> getItems(@PathVariable(required = false) String itemType) {

        if(itemType != null) {
            if(!isValidCategory(itemType)) {
                return null;
            }
            return itemRepository.findByType(itemType);
        }else{
            return itemRepository.findAll();
        }


    }

    @DeleteMapping("/admin/items/{itemId}")
    public String deleteItem(@PathVariable Long itemId) {

            Item item = itemRepository.findItemById(itemId);
            if(item == null) {
                return "Item not found";
            }
            try {
                Shop shop = item.getShop();
                if(shop != null) {
                    shop.getInventory().remove(item);
                    shopRepository.save(shop);
                }
                itemRepository.delete(item);
                return "Item deleted";
            }catch (Exception e) {
                return "Error deleting item" + e.getMessage();
            }




    }

    @PostMapping("/buy")
    public String buyItem( @RequestParam long userId,
                           @RequestParam Long itemId) {
        Person buyer = personRepository.findById(userId);
        if(buyer == null) {
            return "Person not found";
        }

        Item item = itemRepository.findItemById(itemId);
        if(item == null) {
            return "Item not found";
        }


        if(buyer.ownsItem(item)){
            return "You already own this item!";
        }

        if(!buyer.canAfford(item.getPrice())){
            return "You don't have enough points to buy this item";
        }

        try{
            if(!buyer.deductBalance(item.getPrice())){
                return "Purchase failed!";
            }

            Payment payment = new Payment();
            payment.setBuyer(buyer);
            payment.setPurchasedItem(item);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setAmount(item.getPrice());
            payment.setCardNumber("1234567822226666");
            payment.setExpirationDate("01/40");
            payment.setCvv("000");


            buyer.addItem(item);

            paymentController.buy(payment);
            personRepository.save(buyer);
            return "Item Purchased!";
        }catch (Exception e) {
            return "Purchase failed!" + e.getMessage();
        }
    }

    @GetMapping("owned-items/{userId}")
    public List<Item> getOwnedItems(@PathVariable long userId) {
        Person person = personRepository.findById(userId);
        if(person == null) {
            return null;
        }

        return person.getOwnedItems().stream()
                .map(UserItems::getItem)
                .collect(Collectors.toList());
    }



}
