package com.coms309.duality.controllers;

import com.coms309.duality.model.Journal;
import com.coms309.duality.model.Person;
import com.coms309.duality.repository.JournalRepository;
import com.coms309.duality.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/journal")
public class JournalController {

    @Autowired
    JournalRepository journalRepository;
    @Autowired
    private PersonRepository personRepository;

    //create a new free-form journal
    @PostMapping("/{username}/create-journal")
    public String createJournal(@RequestBody Journal newJournal, @PathVariable String username) {
        Person p = personRepository.findByUsername(username);
        if(p != null) {
            // new journal entry owner
            newJournal.setPerson(p);
            // add new journal
            p.getSavedJournals().add(newJournal);
            personRepository.save(p);
            return "Journal has been saved";
        }

        return null;
    }

//    //create a prompted journal
//    @PostMapping("create-prompted-journal")
//    public ResponseEntity<String> createPromptedJournal(@RequestBody Journal newJournal) {
//        String prompt = newJournal.getPrompt();
//        journalRepository.save(newJournal);
//        return new ResponseEntity<>("Your journal has been saved!", HttpStatus.CREATED);
//    }


    // find a past journal by title
    @GetMapping("/retrieve-journal/{title}")
    public String retrieveJournal(@PathVariable String title) {
        Journal j = journalRepository.findByTitle(title);
        if(j != null){
            return j.getBody();
        }
        return "Journal not found";

    }

    //delete journal
    @DeleteMapping("/delete-journal/{title}")
    public String deleteJournal(@RequestBody Journal journal, @PathVariable String title) {
        if (journalRepository.existsByTitle(title)){
            journalRepository.delete(journal);
            return "Journal has been deleted";
        }

        return "Journal does not exist";
    }


    //get all journals
    @GetMapping("/all-journals")
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
//    @GetMapping("/prompt")
//    public Optional<Journal> getPrompt(@RequestParam String prompt_name) {
//        return journalRepository.findByPromptName(prompt_name);
//    }
//

}
