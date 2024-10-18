package com.coms309.duality.model;

import jakarta.persistence.*;

@Entity
@Table(name="checkin")
public class Checkin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="MHRATING")
    private int mentalHealthRating;

    @Column(name="PHRATING")
    private int physicalHealthRating;

    @Column(name="WATERAMOUNT")
    private int waterAmount;

    @Column(name="NOTES")
    private String notes;


    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person p;



    // default constructor
    public Checkin() {}

    public Checkin(int mentalHealthRating, int physicalHealthRating, int waterAmount, String notes) {
        this.mentalHealthRating = mentalHealthRating;
        this.physicalHealthRating = physicalHealthRating;
        this.waterAmount = waterAmount;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMentalHealthRating() {
        return mentalHealthRating;
    }

    public void setMentalHealthRating(int mentalHealthRating) {
        this.mentalHealthRating = mentalHealthRating;
    }

    public int getPhysicalHealthRating() {
        return physicalHealthRating;
    }

    public void setPhysicalHealthRating(int physicalHealthRating) {
        this.physicalHealthRating = physicalHealthRating;
    }

    public int getWaterAmount() {
        return waterAmount;
    }

    public void setWaterAmount(int waterAmount) {
        this.waterAmount = waterAmount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Person getP() {
        return p;
    }

    public void setP(Person p) {
        this.p = p;
    }
}
