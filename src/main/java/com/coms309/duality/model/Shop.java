package com.coms309.duality.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name="shop")
public class Shop extends Payment{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Getter
    @Setter
    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column
    private List<Item> inventory = new ArrayList<>();




    public Shop() {

    }

    public Shop(List<Item> inventory) {

        this.inventory = inventory;


    }

    @Override
    public String toString() {
        return "Shop{" +
                "ShopID=" + id +
                ", inventory='" + inventory + '\'' +
                '}';
    }


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

}
