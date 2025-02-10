package com.coms309.duality.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name="points")
public class Points {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "person_id")
    @JsonIgnore
    private Person person;

    @Enumerated(EnumType.STRING)
    @Column(name = "HABITTYPE")
    private HabitType type;
    @Column(name = "AMOUNT")
    int amount;
    @Column(name = "DATE")
    private LocalDate date;

    public Points() {}

    public Points(Person p, int amount, HabitType type) {
        this.person = p;
        this.amount = amount;
        this.type = type;
        this.date = LocalDate.now();
    }

}
