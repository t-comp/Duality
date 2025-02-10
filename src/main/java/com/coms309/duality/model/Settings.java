package com.coms309.duality.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter
    @Setter
    @Column(name = "DARKMODE")
    private boolean darkMode;

    @Getter
    @Setter
    @Column(name = "LANGUAGE")
    private String language;

    @Getter
    @Setter
    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "person_id")
    private Person person;

    public Settings() {

    }

    public Settings(boolean darkMode, String language) {
        this.darkMode = darkMode;
        this.language = language;
    }

}
