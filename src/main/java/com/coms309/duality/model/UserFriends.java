package com.coms309.duality.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "user_friends")
public class UserFriends {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private Person person;


    public void setId(int id) {
        this.id = id;
    }

}
