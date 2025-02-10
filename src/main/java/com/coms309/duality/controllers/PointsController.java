package com.coms309.duality.controllers;

import com.coms309.duality.model.Person;
import com.coms309.duality.model.Points;
import com.coms309.duality.repository.PersonRepository;
import com.coms309.duality.repository.PointsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/points")
public class PointsController {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    PointsRepository pointsRepository;

    private boolean isAdmin(long userId) {
        Person p = personRepository.findById(userId);
        return p == null || p.isAdmin();
    }

    @GetMapping("/{userId}")
    public int getTotalPoints(@PathVariable long userId) {
        Person p = personRepository.findById(userId);
        if (p != null) {
            return p.getTotalPoints();
        }
        return 0;
    }

    @DeleteMapping("/admin/delete/{userId}")
    public String deletePoints(@PathVariable long userId, @RequestParam long requestId) {
        if (!isAdmin(userId)) {
            return "Sorry, you can't do that.";
        }

        Person p = personRepository.findById(userId);
        if (p != null) {
            List<Points> points = pointsRepository.findByPerson(p);
            pointsRepository.deleteAll(points);
            p.setTotalPoints(0);
            personRepository.save(p);
            return "All points deleted for " + p.getUsername();
        }
        return "User was not found.";
    }
}
