package com.coms309.duality.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

//One-To-Many Table
@Getter
@Setter
@Entity
@Table(name="journal")
public class Journal {

    @ManyToOne()
    @JoinColumn(name="person_id")
    @JsonIgnore
    private Person person;

    @Column(name="TITLE")
    private String title;

    @Column(name="BODY", columnDefinition = "LONGTEXT")
    private String body;

    //journal types (free form or prompted)
    @Column(name="JOURNALTYPE")
    private String journalType;

//    //journal prompts
//    @Column(name="PROMPT")
//    private String prompt;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="DATE")
    private LocalDate date;


    // default constructor
    public Journal() {

    }
    public Journal(String title, String journalType,String body) {
        this.title = title;
        this.journalType = journalType;

        this.body = body;
    }


    // toString() method
    @Override
    public String toString() {
        return  "Journal{" +
                "ID=" + id + '\'' +
                "title='" + title + '\'' +
                ", journalType='" + journalType + '\'' +
                ", body='" + body + '\'' + '}';

    }


    public Journal orElse(Object o) {
        return null;
    }

}
