package com.coms309.duality.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


// tells the database what this is
@Setter
@Entity
@Table(name="activity")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Getter
    @Column(name="ACTIVITYNAME")
    private String activityName;
    @Getter
    @Column(name="CATEGORY")
    private String category;
    @Getter
    @Column(name="LENGTH")
    private int length;
    @Getter
    @Column(name="DESCRIPTON")
    private String description;
    @Getter
    @Column(name="GUIDE", columnDefinition = "TEXT")
    private String guide;

    @Getter
    @Column(name="VIDEOURL")
    private String videoUrl;
    @Getter
    @Column(name="VIDEOTITLE")
    private String videoTitle;


//    @Getter
//    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
//    private List<YoutubeVideo> youtubeVideos = new ArrayList<>();


    // default constructor
    public Activity() {

    }

    public Activity(String activityName, String category, int length, String description, String guide) {
        this.activityName = activityName;
        this.category = category;
        this.length = length;
        this.description = description;
        this.guide = guide;
    }

    public long getId() {
        return id;
    }

}
