package com.coms309.duality.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

//One-To-Many Table
@Entity
@Table(name="journal")
public class Journal {

    @ManyToOne()
    @JoinColumn(name="person_id")
    @JsonIgnore
    private Person person;

    @Column(name="TITLE")
    private String title;

    @Column(name="BODY")
    private String body;

    //journal types (free form or prompted)
    @Column(name="JOURNALTYPE")
    private String journalType;

    //journal prompts
    @Column(name="PROMPT")
    private String prompt;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    // default constructor
    public Journal() {

    }
    public Journal(String title, String journalType, String prompt,String body) {
        this.title = title;
        this.journalType = journalType;
        this.prompt = prompt;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public String getJournalType() {
        return journalType;
    }

    public void setJournalType(String journalType) {
        this.journalType = journalType;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
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


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }


    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Journal orElse(Object o) {
        return null;
    }
}
