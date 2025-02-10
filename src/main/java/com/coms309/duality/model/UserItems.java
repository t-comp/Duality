package com.coms309.duality.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="user_items")
public class UserItems {
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="person_id")
    private Person person;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name="item_id")
    private Item item;

    private LocalDateTime purchaseDate;

    public UserItems(){}

    public UserItems(Person person, Item item) {
        this.person = person;
        this.item = item;
        this.purchaseDate = LocalDateTime.now();
    }

}
