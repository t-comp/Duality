package com.coms309.duality.controllers;

import com.coms309.duality.model.Activity;
import com.coms309.duality.model.Journal;
import com.coms309.duality.model.Person;
import com.coms309.duality.repository.ActivityRepository;
import com.coms309.duality.repository.JournalRepository;
import com.coms309.duality.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class PersonController {

    @Autowired
    PersonRepository personRepository;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    JournalRepository journalRepository;

    private BCryptPasswordEncoder pwEncoder = new BCryptPasswordEncoder();


    /**
     * Allows user to save activity.
     * @param userId - the ID of the user
     * @param newJournal - new journal
     * @return success message or not found message
     */
    @PostMapping("/{userId}/create-journal")
    public String createJournal(@RequestBody Journal newJournal, @PathVariable long userId) {
        Person p = personRepository.findById(userId);
        if(p != null) {
            // new journal entry owner
            newJournal.setPerson(p);
            // add new journal
            p.getSavedJournals().add(newJournal);
            personRepository.save(p);
            return "Journal has been saved";
        }

        return "Unable to make this journal!";
    }

    /**
     * Allows user to save activity.
     * @param userId - the ID of the user
     * @param activityId - the ID of the activity user will save
     * @return success message or not found message
     */
    @PostMapping("/{userId}/saved/{activityId}")
    public String saveActivity(@PathVariable Long userId, @PathVariable Long activityId) {
        Person p = personRepository.findById(userId).orElse(null);
        Activity a = activityRepository.findById(activityId).orElse(null);

        if (p != null && a != null) {
           // get saved activities and add new saved activity
           p.getSavedActivities().add(a);
           personRepository.save(p);
           return a.getActivityName() + " has been saved!";
        } else {
            return "Error, not found!";
        }
    }

    /**
     * Allows user to unsave an activity.
     * @param userId - the ID of the user
     * @param activityId - the ID of the activity user will save
     * @return success message or not found message
     */
    @PutMapping("/{userId}/unsaved/{activityId}")
    public String unsaveActivity(@PathVariable Long userId, @PathVariable long activityId) {
        Person p = personRepository.findById(userId).orElse(null);
        Activity a = activityRepository.findById(activityId).orElse(null);

        if (p != null && a != null) {
            // saved activities
            List<Activity> savedActivities = p.getSavedActivities();
            // if saved activities has specific saved activity
            if (savedActivities.contains(a)) {
                // remove her.
                savedActivities.remove(a);
                personRepository.save(p);
                return a.getActivityName() + " unsaved!";
            } else {
                return "This activity is not saved.";
            }
        } else {
            return "Error, not found!";
        }
    }

    /**
     * Retrieves a specific user's saved activities.
     * @param userId - the ID of the user
     * @return list of activities saved by a user
     */
    @GetMapping("/{userId}/saved-activities")
    public List<Activity> getSavedActivities(@PathVariable Long userId) {
        Person p = personRepository.findById(userId).orElse(null);
        if (p != null) {
            return p.getSavedActivities();
        }
        return new ArrayList<>();
    }

    /**
     * Helper method - validates password based on length
     * @param password - the password that needs to be validated
     * @return true or false depending on if password meets requirements
     */
    private boolean isValidPassword(String password) {
        return password.length() >= 7;
    }

    /**
     * Helper method - validates email using regex patterm
     * @param email - email to validate
     * @return true or false if the email matches regex pattern
     */
    private boolean isValidEmail(String email) {
        String emailRegexPattern = "^[a-zA-Z0-9_.±]+@[a-zA-Z0-9-]+.[a-zA-Z0-9-.]+$";
        return email.matches(emailRegexPattern);
    }


    /**
     * C: Creates a new account for a User. Uses email and password validation to ensure they meet the requirements.
     * Additionally, hashes the User's password before saving the newly created user.
     *
     * @param newPerson - new user to be created
     * @return - success message to user
     */
    @PostMapping("/create-account")
    public String createAccount(@RequestBody Person newPerson) {
        // validation
        if (!isValidPassword(newPerson.getPassword())) {
            return "Your password must be at least 7 characters.";
        } if (!isValidEmail(newPerson.getEmail())) {
            return "Your email address is invalid.";
        }
        // if user/email exists
        if (personRepository.existsByUsername(newPerson.getUsername())) {
            return "You'll have to choose a new username!";
        } if (personRepository.existsByEmail(newPerson.getEmail())) {
            return "You'll have to choose a new email!";
        }
        // password hashing
        String hashedPassword = pwEncoder.encode(newPerson.getPassword());
        newPerson.setPassword(hashedPassword);
        // set account to be active
        newPerson.setAccountStatus(true);
        // set user type to be average user
        newPerson.setUserType("AVERAGE");
        // save new user
        personRepository.save(newPerson);
        // success message
        return "Your account has been created!";
    }


    /**
     * R: Retieves a specific user by their username.
     * @param username - the username to be retrieved.
     * @return the user
     */
    @GetMapping("/{username}")
    public Person getUser(@PathVariable("username") String username) {
        return personRepository.findByUsername(username);

    }


    /**
     * D: Deletes user and all info by user ID.
     * @param userId - id of user
     * @return success or not found message
     */
    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable long userId) {
        if (personRepository.existsById(userId)) {
            personRepository.deleteById(userId);
            return "User has been deleted.";
        }
        return "User not real.";
    }

    /**
     * D: Deletes all users. Admin usage only.
     * @return success message that all users have been deleted
     */
    @DeleteMapping("/admin/delete-all")
    public String deleteAllUsers() {
        personRepository.deleteAll();
        return "All users are gone.";
    }

    /**
     * L: List all users. Admin usage only.
     * @return list of all users
     */
    @GetMapping
    public List<Person> getAllUsers() {
        return personRepository.findAll();
    }

    /**
     * Logs a specific user in with password and username.
     * @param username - user username
     * @param password - user password
     * @return success or fail message
     */
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        if(username == null || password == null) {
            return "Invalid username or password!";
        }

        if(personRepository.existsByUsername(username)) {
            Person person = personRepository.findByUsername(username);
            if(pwEncoder.matches(password, person.getPassword())) {
                person.setLoggedIn(true);
                personRepository.save(person);
                return "You are logged in!";
            }
        }
        return "Invalid stuff!";
    }

    /**
     * Logs a specific user in with password and email.
     * @param email - user email
     * @param password - user password
     * @return success or fail message
     */
    @GetMapping("/{email}/{password}")
    public String loginEmail(@PathVariable String email, @PathVariable String password) {
        if(email == null || password == null) {
            return "Invalid username or password!";
        }
        if(personRepository.existsByEmail(email)) {
            Person person = personRepository.findByEmail(email);
            if(pwEncoder.matches(password, person.getPassword())) {
                return "You are logged in!";
            }
        }
        return "Invalid stuff!";
    }



    /**
     * Logs out a specific user with their username
     * @param username - user username
     * @return a success or failure message
     */
    @PostMapping("/logout")
    public String logout(@RequestParam String username) {
        Person p = personRepository.findByUsername(username);
        if (p != null && p.isLoggedIn()) {
            p.setLoggedIn(false);
            personRepository.save(p);
            return "You've been logged out!";
        }
        return "Something silly is going on here...please try again!";
    }

    /**
     * U: Updates password for a specific user with their email and new password
     * @param userEmail - user email
     * @param newPassword - new user password
     * @return success or fail message
     */
    @PutMapping("/resetPassword")
    public String resetPassword(@RequestParam("email") String userEmail, @RequestParam("newPassword") String newPassword) {
        Person p = personRepository.findByEmail(userEmail);

        if (p == null) {
            return userEmail + " is invalid!";
        }
        // reset password and hash
        String hashPW = pwEncoder.encode(newPassword);
        p.setPassword(hashPW);

        personRepository.save(p);

        return "Your password has been reset!";
    }

}