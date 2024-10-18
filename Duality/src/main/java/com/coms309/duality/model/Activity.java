package com.coms309.duality.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


// tells the database what this is
@Entity
@Table(name="activity")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name="ACTIVITYNAME")
    private String activityName;
    @Column(name="CATEGORY")
    private String category;
    @Column(name="LENGTH")
    private int length;
    @Column(name="DESCRIPTON")
    private String description;

    @ManyToMany(mappedBy="savedActivities")
    List<Person> saved = new ArrayList<>();


    // default constructor
    public Activity() {

    }

    public Activity(String activityName, String category, int length, String description) {
        this.activityName = activityName;
        this.category = category;
        this.length = length;
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
