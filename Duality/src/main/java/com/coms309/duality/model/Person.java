package com.coms309.duality.model;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "person")
public class Person {


    // user ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userID;

    // username
    @Column(name = "USERNAME", nullable = false)
    private String username;

    // email
    @Column(name = "EMAIL")
    private String email;

    // password
    @Column(name = "PASSWORD")
    private String password;

    // first name
    @Column(name = "FIRSTNAME")
    private String firstName;

    // last name
    @Column(name = "LASTNAME")
    private String lastName;

    // user type - admin, guest user, regular user, premium user
    @Column(name = "USERTYPE", nullable = false)
    private String userType;

    // active or non-active account
    @Column(name = "ISACTIVE", nullable = false)
    private boolean isAccountActive;

    //reset password token
//    @Column(name = "RESET_PASSWORD_TOKRN")
//    private String reset_password_token;

    private boolean isLoggedIn = false;


    @ManyToMany
    @JoinTable(
            name = "saved_activity",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "activity_id"))
    private List<Activity> savedActivities = new ArrayList<>();


    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)

    private List<Journal> savedJournals = new ArrayList<>();

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)

    private List<Checkin> personCheckins = new ArrayList<>();

    public Person() {

    }

    // constructor
    public Person(String username, String email, String password, String firstName, String lastName, String userType, boolean isAccountActive) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = "DEFAULT";
        this.isAccountActive = isAccountActive;

    }


    // getters and setters
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public boolean getAccountStatus() {
        return isAccountActive;
    }

    public void setAccountStatus(boolean active) {
        isAccountActive = active;
    }

    public List<Activity> getSavedActivities() {
        return savedActivities;
    }

    public void setSavedActivities(List<Activity> savedActivities) {
        this.savedActivities = savedActivities;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public List<Journal> getSavedJournals() {
        return savedJournals;
    }

    public void setSavedJournals(List<Journal> savedJournals) {
        this.savedJournals = savedJournals;
    }

    public List<Checkin> getPersonCheckins() { return personCheckins; }

    public void setPersonCheckins(List<Checkin> personCheckins) {this.personCheckins = personCheckins;}
    // toString() method
    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
