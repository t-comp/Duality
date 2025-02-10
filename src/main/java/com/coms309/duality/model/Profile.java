package com.coms309.duality.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="profile")
public class Profile {

    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
    private long id;

    @Getter
    @Column(name="NAME")
    private String name;

    @Setter
    @Column(name="EMAIL")
    private String email;

    @Setter
    @Getter
    @Column(name="PRONOUNS")
    private String pronouns;

    @Setter
    @Getter
    @Column(name="BIO")
    private String bio;

    //DATE OF BIRTH
    @Setter
    @Getter
    @Column(name="DOB")
    private String dob;

    @Setter
    @Column(name = "TOTALPOINTS")
    private Integer totalPoints;

    @Setter
    @Column(name = "profile_pic")
    private String profilePic;

    @Getter
    @Setter
    @OneToOne
    @JsonIgnore
    private Person person;

    //default constructor
    public Profile() {}


    //constructor with params
    public Profile(String name, String email, String bio, String dob, Integer totalPoints) {
        this.name = name;
        this.email = email;
        this.bio = bio;
        this.dob = dob;
        this.totalPoints = totalPoints;
    }

    // toString() method
    @Override
    public String toString() {
        return "User{" +
                "userID=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", pronouns='" + pronouns + '\'' +
                ", bio='" + bio + '\'' +
                ", dob='" + dob + '\'' +
                '}';
    }

    public void setName(String firstName, String lastName) {
        this.name = firstName + " " + lastName;
    }

    public String getEmail() {
        return email;
    }


    public String getProfilePic() {
        return profilePic;
    }

    public Integer getTotalPoints() {
        return totalPoints;
    }

}
