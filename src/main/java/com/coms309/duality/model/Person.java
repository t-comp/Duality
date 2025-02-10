package com.coms309.duality.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Entity
@Table(name = "person")
public class Person {


    // getters and setters
    // user ID
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userID;

    // username
    @Getter
    @Setter
    @JsonIgnore
    @Column(name = "USERNAME", nullable = false)
    private String username;

    // email
    @Getter
    @Setter
    @Column(name = "EMAIL")
    private String email;

    // password
    @Getter
    @Setter
    @Column(name = "PASSWORD")
    private String password;

    // first name
    @Getter
    @Setter
    @Column(name = "FIRSTNAME")
    private String firstName;

    // last name
    @Getter
    @Setter
    @Column(name = "LASTNAME")
    private String lastName;

    // active or non-active account
    @Column(name = "ISACTIVE", nullable = false)
    private boolean isAccountActive;

    private boolean isLoggedIn = false;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "USERTYPE", nullable = false)
    private UserType type;

    @Getter
    @Setter
    @ManyToMany
    @JoinTable(
            name = "saved_activity",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "activity_id"))
    @JsonIgnore
    private List<Activity> savedActivities = new ArrayList<>();

    @Getter
    @Setter
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Journal> savedJournals = new ArrayList<>();

    @Getter
    @Setter
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Checkin> personCheckins = new ArrayList<>();


    @Getter
    @Setter
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserItems> ownedItems = new ArrayList<>();

//    @Getter
//    @Setter
//    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Payment> personPayments = new ArrayList<>();

    @Getter
    @Setter
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name ="profile_id")
    private Profile userProfile;


    @Getter
    @Setter
    @ManyToMany
    @JoinTable(name = "user_friends", joinColumns = @JoinColumn(name = "userId"), inverseJoinColumns = @JoinColumn(name = "FriendId"))
    private Set<Person> userFriends;

    @Getter
    @Setter
    @ManyToMany
    @JoinTable(name = "block_users", joinColumns = @JoinColumn(name = "blockUserId") , inverseJoinColumns = @JoinColumn(name = "targetUserId") )
    private Set<Person> blockUsers;

    @Getter
    @Setter
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    @Getter
    @Setter
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "shop_id")
    private Shop shop;


    @Column(name = "TOTALPOINTS")
    private Integer totalPoints;

    public Person() {
        this.type = UserType.DEFAULT;
        this.totalPoints = 0;

    }
    // constructor
    public Person(String username, String email, String password, String firstName, String lastName, boolean isAccountActive, int totalPoints) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isAccountActive = isAccountActive;
        this.type = UserType.DEFAULT;
        this.totalPoints = totalPoints;

    }
    public void setUserID(int userID) {
        this.userID = userID;
    }

    public boolean getAccountStatus() {
        return isAccountActive;
    }

    public void setAccountStatus(boolean active) {
        isAccountActive = active;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public boolean isAccountActive() {
        return isAccountActive;
    }

    public void setAccountActive(boolean accountActive) {
        isAccountActive = accountActive;
    }

    public void addUserFriends(Person p) {
        if (CollectionUtils.isEmpty(this.userFriends)) {
            this.userFriends = new HashSet<>();
        }
        this.userFriends.add(p);
    }

    public void addBlockUsers(Person p){
        if(CollectionUtils.isEmpty(this.blockUsers)){
            this.blockUsers = new HashSet<>();
        }
        this.blockUsers.add(p);
    }

    public Integer getTotalPoints() {
        if (totalPoints == null) {
            return 0;
        }
        return totalPoints;
    }
    public void setTotalPoints(Integer totalPoints) {
        if (totalPoints == null) {
            this.totalPoints = 0;
        } else {
            this.totalPoints = totalPoints;
        }
    }
    public boolean canAfford(int price) {
        return this.totalPoints >= price;
    }
    public boolean deductBalance(int amount) {
        if (canAfford(amount)) {
            this.totalPoints -= amount;
            return true;
        }
        return false;
    }

    public void addItem(Item item) {
        UserItems userItem = new UserItems(this, item);
        this.ownedItems.add(userItem);
    }

    public boolean ownsItem(Item item) {
        return ownedItems.stream()
                .anyMatch(userItem -> userItem.getItem().getId().equals(item.getId()));
    }


    public boolean isAdmin() {
        return this.getType() != UserType.ADMIN;
    }

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
