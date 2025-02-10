package com.coms309.duality.controllers;

import com.coms309.duality.model.HabitType;
import com.coms309.duality.model.Journal;
import com.coms309.duality.model.Person;
import com.coms309.duality.model.Points;
import com.coms309.duality.repository.JournalRepository;
import com.coms309.duality.repository.PersonRepository;
import com.coms309.duality.repository.PointsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/journal")
public class JournalController {

    @Autowired
    JournalRepository journalRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PointsRepository pointsRepository;

    //create a new free-form journal
    @PostMapping("/{username}/create-journal")
    public String createJournal(@RequestBody Journal newJournal, @PathVariable String username) {
        Person p = personRepository.findByUsername(username);
        if(p != null) {
            // new journal entry owner
            newJournal.setPerson(p);
            // add new journal
            LocalDate date = LocalDate.now();

            // point calculation
            int pointsEarned = calcJournalPoints(newJournal);
            Points points = new Points(p, pointsEarned, HabitType.JOURNAL);

            Integer currentPoints = p.getTotalPoints();
            if (currentPoints == null) {
                p.setTotalPoints(pointsEarned);
            } else {
                p.setTotalPoints(currentPoints + pointsEarned);
            }

            p.getSavedJournals().add(newJournal);
            newJournal.setDate(date);
            pointsRepository.save(points);
            journalRepository.save(newJournal);
            personRepository.save(p);
            return "Journal has been saved. You earned " + pointsEarned + " points!";
        }

        return null;
    }


    // find a past journal by title
    @GetMapping("/{userId}/{title}")
    public String retrieveJournal(@PathVariable long userId, @PathVariable String title) {
        Person p = personRepository.findById(userId);
        List<Journal> savedJournals = p.getSavedJournals();
        for (Journal journal : savedJournals) {
            if(journal.getTitle().equals(title)) {
                return journal.getBody();
            }
        }

        return "Journal not found";

    }

    @DeleteMapping("/delete-all-journals")
    public String deleteAllJournals() {
        journalRepository.deleteAll();
        return "All journals deleted";
    }

    //delete journal
    @DeleteMapping("/delete-journal/{title}")
    public String deleteJournal(@PathVariable String title) {
        Journal j = journalRepository.findByTitle(title);
        if (journalRepository.existsByTitle(title)){
            journalRepository.delete(j);
            return "Journal has been deleted";
        }

        return "Journal does not exist";
    }


    //get all journals
    @GetMapping("/admin/all-journals")
    public List<Journal> getAllJournals() { return journalRepository.findAll(); }

    //admin can change titles of journals with the journal id
    @PutMapping("/admin/update-title/{id}")
    public Journal updateTitle(@PathVariable long id, @RequestBody Journal updatedjournal) {
        Journal j = journalRepository.findById(id);
        if(j == null) {
            return null;
        }
        j.setTitle(updatedjournal.getTitle());
        return journalRepository.save(j);
    }

    @GetMapping("/{date}")
    public List<Journal> getJournalsByDate(@PathVariable LocalDate date) {
        return journalRepository.getJournalByDate(date);
    }

    private int calcJournalPoints(Journal journal) {
        int points = 0;

        points += 100;

        String[] words = journal.getBody().split("\\s+");
        int wordCount = words.length;
        if (wordCount >= 500) {
            points += 300;
        } else if (wordCount >= 200) {
            points += 200;
        } else if (wordCount >= 100) {
            points += 100;
        }
        return points;
    }


}
