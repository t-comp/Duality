package com.coms309.duality.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;


public class Payment {

    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    private String cardNumber;
    @Getter
    @Setter
    private String expirationDate;
    @Getter
    @Setter
    private String cvv;
    @Getter
    @Setter
    private double amount;
    private boolean isBought;

    @Getter
    @Setter
    private String paymentType;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person buyer;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item purchasedItem;

    @Setter
    @Getter
    private LocalDateTime paymentDate;

    public Payment() {
        this.isBought = false;
    }

    public boolean isBought() {
        return isBought;
    }

    public void setBought(boolean bought) {
        isBought = bought;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
