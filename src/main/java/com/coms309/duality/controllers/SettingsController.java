package com.coms309.duality.controllers;

import com.coms309.duality.model.Person;
import com.coms309.duality.model.Settings;
import com.coms309.duality.repository.PersonRepository;
import com.coms309.duality.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settings")
public class SettingsController {

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private PersonRepository personRepository;

    @PostMapping("/{userId}")
    public Settings setSettings(@PathVariable long userId) {
        Person p = personRepository.findById(userId);
        if (p != null) {
            Settings s = settingsRepository.findByPerson(p);

            if (s == null) {
                s = new Settings();
                s.setLanguage("en");
                s.setDarkMode(false);
                s.setPerson(p);
                return settingsRepository.save(s);
            }
            return s;
        }
        return null;
    }

    @GetMapping("/{userId}")
    public Settings getSettings(@PathVariable long userId) {
        Person p = personRepository.findById(userId);
        if (p == null) {
            return null;
        }
        return settingsRepository.findByPerson(p);
    }

    @PutMapping("/{userId}/darkmode")
    public Settings updateDarkMode(@PathVariable long userId, @RequestParam boolean isDarkMode) {
        Person p = personRepository.findById(userId);
        if (p != null) {
            Settings s = settingsRepository.findByPerson(p);
            if (s == null) {
                s = new Settings();
                s.setPerson(p);
            }
            s.setDarkMode(isDarkMode);
            return settingsRepository.save(s);
        }
        return null;
    }


    @PutMapping("/{userId}/language")
    public Settings updateLanguage(@PathVariable long userId, @RequestParam String language) {
        Person p = personRepository.findById(userId);
        if (p != null) {
            Settings s = settingsRepository.findByPerson(p);
            if (s == null) {
                s = new Settings();
                s.setPerson(p);
            }
            s.setLanguage(language);
            return settingsRepository.save(s);
        }
        return null;
    }
}
