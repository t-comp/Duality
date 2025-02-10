package com.coms309.duality.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "block_users")
public class BlockUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="targetUserId")
    private long targetUserId;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name="blockUserId", nullable = false)
    private Person person;


    public void setTargetUserId(int targetUserId) {
        this.targetUserId = targetUserId;
    }

}
