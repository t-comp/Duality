package com.coms309.duality.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="checkin")
public class Checkin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter
    @Setter
    @Column(name="MHRATING")
    private int mentalHealthRating;

    @Getter
    @Setter
    @Column(name="PHRATING")
    private int physicalHealthRating;

    @Getter
    @Setter
    @Column(name="WATERAMOUNT")
    private int waterAmount;

    @Getter
    @Setter
    @Column(name="NOTES")
    private String notes;

    @Getter
    @Setter
    @Column(name="DATE")
    private LocalDate date;

    @Setter
    @Column(name="STREAK")
    private int streak;

    @Getter
    @Setter
    @Column(name="STEPS")
    private int steps;

    @Getter
    @Setter
    @Column(name="SLEEP")
    private int sleep;

    @Getter
    @Setter
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="person_id", nullable = false)
    private Person person;


    // default constructor hehe
    public Checkin() {}

    public Checkin(int mentalHealthRating, int physicalHealthRating, int waterAmount, String notes, int steps, int sleep) {
        this.mentalHealthRating = mentalHealthRating;
        this.physicalHealthRating = physicalHealthRating;
        this.waterAmount = waterAmount;
        this.notes = notes;
        this.steps = steps;
        this.sleep = sleep;
    }

}
