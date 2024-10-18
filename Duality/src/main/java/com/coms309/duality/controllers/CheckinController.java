package com.coms309.duality.controllers;


import com.coms309.duality.model.Checkin;
import com.coms309.duality.repository.CheckinRepository;
import com.coms309.duality.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/checkin")
public class CheckinController {

    @Autowired
    private CheckinRepository checkinRepository;
    @Autowired
    private PersonRepository personRepository;

    // basic create
    @PostMapping
    public String createCheckin(@RequestBody Checkin newCheckin) {
        checkinRepository.save(newCheckin);
        return "Checkin created";
    }

    // basic read
    @GetMapping("/{id}")
    public Checkin getCheckin(@PathVariable long id) {
        Checkin c = checkinRepository.findById(id).orElse(null);
        return c;
    }

    // basic update
    @PutMapping("/{id}")
    public Checkin updateCheckin(@PathVariable long id, @RequestBody Checkin updatedCheckin) {
        Checkin c = checkinRepository.findById(id).orElse(null);
        if (c == null) {
            return null;
        }

        // update
        c.setMentalHealthRating(updatedCheckin.getMentalHealthRating());
        c.setPhysicalHealthRating(updatedCheckin.getPhysicalHealthRating());
        c.setWaterAmount(updatedCheckin.getWaterAmount());
        c.setNotes(updatedCheckin.getNotes());

        // save updated checkin
        return checkinRepository.save(c);
    }

    // basic delete
    @DeleteMapping("/{id}")
    public String deleteCheckin(@PathVariable long id) {
        Checkin c = checkinRepository.findById(id).orElse(null);
        if (c == null) {
            return null;
        }

        checkinRepository.delete(c);
        return "Checkin has been deleted";
    }

    // basic list
    @GetMapping("")
    public List<Checkin> getAllCheckins() {
        return checkinRepository.findAll();
    }

}
