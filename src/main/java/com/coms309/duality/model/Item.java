package com.coms309.duality.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="item")
public class Item {


    @Getter
    @Setter
    @ManyToOne()
    @JoinColumn(name="shop_id")
    @JsonIgnore
    private Shop shop;

    @Getter
    @Setter
    @NotNull(message = "Item name is required")
    @Column(name="ITEMNAME")
    private String itemName;

    @Getter
    @Setter
    @Column(name="TYPE")
    private String type;


    @Setter
    @Getter
    @Column(name="PRICE")
    private Integer price;



    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String pictureURL;


    public Item(String type, String itemName, Integer price, String pictureURL) {


        this.type = type;
        this.itemName = itemName;
        this.price = price;
        this.pictureURL = pictureURL;
    }


    public Item() {

    }


}
