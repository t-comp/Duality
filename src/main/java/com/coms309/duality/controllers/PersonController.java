package com.coms309.duality.controllers;

import com.coms309.duality.model.*;
import com.coms309.duality.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.coms309.duality.model.UserType.ADMIN;

/**
 * Controller for Duality users. Handles user operations and manages user data.
 *
 * @author Taylor Bauer and Abby Van Der Brink
 */

@RestController
@RequestMapping("/user")
public class PersonController {

    @Autowired
    PersonRepository personRepository;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    JournalRepository journalRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    CheckinRepository checkinRepository;

    @Autowired
    PointsRepository pointsRepository;

    private final BCryptPasswordEncoder pwEncoder = new BCryptPasswordEncoder();

    private Person validUser(long userId) {
        Person p = personRepository.findById(userId);
        if (p == null || !p.isLoggedIn()) {
            throw new IllegalStateException("You must log in!");
        }
        return p;
    }

    private Person validAdmin(long userId) {
        Person p = validUser(userId);
        if (p.getType() != UserType.ADMIN) {
            throw new IllegalStateException("You are not admin!");
        }
        return p;
    }

    private Person validPremium(long userId) {
        Person p = validUser(userId);
        if (p.getType() != UserType.PREMIUM && p.getType() != UserType.ADMIN) {
            throw new IllegalStateException("Premium access required");
        }
        return p;
    }

    @PostMapping("/{userId}/create-profile")
    public Profile createProfile(@PathVariable long userId, @RequestBody Profile profile) {
        Person p = personRepository.findById(userId);
        profile.setPerson(p);
        profile.setName(p.getFirstName(), p.getLastName());
        profile.setEmail(p.getEmail());
        profile.setTotalPoints(p.getTotalPoints());
        p.setUserProfile(profile);
        if(profile.getBio() == null && profile.getPronouns() == null && profile.getDob() == null){
            return null;
        }
        return profileRepository.save(profile);
    }

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

            //save date journal is made
            LocalDate date = LocalDate.now();
            newJournal.setDate(date);

            // add new journal
            p.getSavedJournals().add(newJournal);
            personRepository.save(p);
            return "Journal has been saved";
        }

        return "Unable to make this journal!";
    }

    /**
     *
     * @param userId
     * @param title
     * @param updatedjournal
     * @return saved journal
     * user can edit the body of their own journal
     */
    @PutMapping("/{userId}/{title}")
     public Journal updateTitle(@PathVariable long userId, @PathVariable String title, @RequestBody Journal updatedjournal) {
        Person p = personRepository.findById(userId);
        List<Journal> savedJournals = p.getSavedJournals();
        for (Journal journal : savedJournals) {
            if (journal.getTitle().equals(title)) {
                journal.setBody(updatedjournal.getBody());
                return journalRepository.save(journal);
            }
        }
        return null;
    }

    @GetMapping("/{userId}/all-journals")
    public List<Journal> getAllJournals(@PathVariable long userId) {
        Person p = personRepository.findById(userId);
        return p.getSavedJournals();
    }

    /**
     * Sets a user as an admin.
     *
     * @param userId - the id of the user
     * @return a message indicating the outcome of the operation
     */
    @PutMapping("/set-admin/{userId}")
    public String setAdmin(@PathVariable long userId) {
        Person p = personRepository.findById(userId);
        if (p != null) {
            p.setType(ADMIN);
            personRepository.save(p);
            return "Saved as admin";
        }

        return "Error";
    }


    /**
     * Allows user to save activity.
     *
     * @param userId - the id of the user
     * @param activityId - the id of the activity
     * @return success message or not found message
     */
    @PostMapping("/{userId}/saved/{activityId}")
    public String saveActivity(@PathVariable Long userId, @PathVariable Long activityId) {
        try {
            Person p = validUser(userId);
            Activity a = activityRepository.findById(activityId).orElse(null);
            if (a == null) {
                return "Activity not found";
            }
            p.getSavedActivities().add(a);
            personRepository.save(p);
            return a.getActivityName() + " has been saved!";
        } catch (IllegalStateException e) {
            return "Error" + e.getMessage();
        }
    }

    /**
     * Allows user to unsave an activity.
     *
     * @param userId - the id of the user
     * @param activityId - the id of the activity user
     * @return success message or not found message
     */
    @PutMapping("/{userId}/unsaved/{activityId}")
    public String unsaveActivity(@PathVariable Long userId, @PathVariable long activityId) {
        try {
            Person p = validUser(userId);
            Activity a = activityRepository.findById(activityId).orElse(null);
            if (a == null) {
                return "Activity not found";
            }
            List<Activity> savedActivities = p.getSavedActivities();
            if (savedActivities.contains(a)) {
                savedActivities.remove(a);
                personRepository.save(p);
                return a.getActivityName() + " unsaved!";
            }
            return "This activity is not saved.";
        } catch (IllegalStateException e) {
            return "Error";
        }
    }

    /**
     * Allows a user to update their first name, last name, and email.
     *
     * @param userId - the id of the user
     * @param updatedPerson - the persons updated information
     * @return a success message
     */
    @PutMapping("/updateUserInfo/{userId}")
    public String updateUserInfo(@PathVariable long userId, @RequestBody Person updatedPerson) {
        Person p = personRepository.findById(userId);
        if (p != null) {
            p.setFirstName(updatedPerson.getFirstName());
            p.setLastName(updatedPerson.getLastName());
            p.setEmail(updatedPerson.getEmail());
            personRepository.save(p);
        }
        return "Person updated!";
    }


//    @GetMapping("/{userId}/saved-activities")
//    public List<Activity> getSavedActivities(@PathVariable Long userId) {
//        Person p = personRepository.findById(userId).orElse(null);
//        if (p != null) {
//            return p.getSavedActivities();
//        }
//        return new ArrayList<>();
//    }

    /**
     * Validates password based on length
     *
     * @param password - the password that needs to be validated
     * @return true or false depending on if password meets requirements
     */
    private boolean isValidPassword(String password) {
        return password.length() >= 7;
    }

    /**
     * Validates email using regex pattern.
     *
     * @param email - email to validate
     * @return true or false if the email matches regex pattern
     */
    private boolean isValidEmail(String email) {
        String emailRegexPattern = "^[a-zA-Z0-9_.±]+@[a-zA-Z0-9-]+.[a-zA-Z0-9-.]+$";
        return email.matches(emailRegexPattern);
    }


    /**
     * Creates a new account for a User. Uses email and password validation to ensure they meet the requirements.
     * Additionally, hashes the User's password before saving the newly created user.
     *
     * @param newPerson - new user to be created
     * @return a message indicating the outcome of the operations
     */
    @PostMapping("/create-account")
    public String createAccount(@RequestBody Person newPerson) {
        // validation first and last name
        if (newPerson.getFirstName() == null || newPerson.getFirstName().isEmpty()) {
            return "Sorry Josh/Anthony, please put a first name.";
        }
        if (newPerson.getLastName() == null || newPerson.getLastName().isEmpty()) {
            return "Sorry Josh/Anthony, please put a last name.";
        }
        // username validation
        if (newPerson.getEmail() == null || newPerson.getUsername().length() < 4) {
            return "Username must be at least 4 characters.";
        } if (personRepository.existsByUsername(newPerson.getUsername())) {
            return "You'll have to choose a new username!";
        }

        if (!isValidEmail(newPerson.getEmail())) {
            return "Your email address is invalid.";
        } if (personRepository.existsByEmail(newPerson.getEmail())) {
            return "You'll have to choose a new email!";
        }

        // password validation
        if (!isValidPassword(newPerson.getPassword())) {
            return "Your password must be at least 7 characters.";
        }
        // password hashing
        String hashedPassword = pwEncoder.encode(newPerson.getPassword());
        newPerson.setPassword(hashedPassword);
        // set account to be active
        newPerson.setAccountStatus(true);
        // save new user
        personRepository.save(newPerson);
        // success message
        return "Your account has been created!";
    }


    /**
     * Retrieves a specific user by their username.
     *
     * @param username - the username to be retrieved
     * @return the user
     */
    @GetMapping("/{username}")
    public Person getUser(@PathVariable("username") String username) {
        return personRepository.findByUsername(username);

    }


    /**
     * Deletes user and all info by user ID. Admin usage only.
     *
     * @param userId - id of user
     * @return a message indicating the outcome of the operation
     */
    @DeleteMapping("/delete/{userId}")
    public String deleteUser(@PathVariable long userId, @RequestParam long requestId) {
        try {
            validAdmin(requestId);
            Person p = personRepository.findById(userId);
            if (p != null) {
                // delete points
                List<Points> points = pointsRepository.findByPerson(p);
                pointsRepository.deleteAll(points);
                // delete checkins
                List<Checkin> checkins = checkinRepository.findByPersonOrderByDateDesc(p);
                checkinRepository.deleteAll(checkins);
                // delete person
                personRepository.delete(p);
                return "User deleted.";
            }
            return "User not found.";
        } catch (IllegalStateException e) {
            return "Error";
        }
    }


    /**
     * Deletes all users. Admin usage only.
     *
     * @return success message that all users have been deleted
     */
    @DeleteMapping("/admin/delete-all")
    public String deleteAllUsers(@RequestBody long userId) {
        try {
            validAdmin(userId);
            personRepository.deleteAll();
            return "All users have been deleted.";
        } catch (IllegalStateException e) {
            return "Error";
        }
    }
    /**
     * List all users. Admin usage only.
     *
     * @return list of all users
     */
    @GetMapping("admin/all")
    public List<Person> getAllUsers(@RequestParam long userId) {
        try {
            validAdmin(userId);
            return personRepository.findAll();
        } catch (IllegalStateException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Logs a specific user in with password and username.
     *
     * @param username - user username
     * @param password - user password
     * @return success or fail message
     */
    @PostMapping("/login/{userId}")
    public String login(@RequestParam String username, @RequestParam String password, @PathVariable long userId) {
        if(username.isEmpty() || password.isEmpty()) {
            return "Invalid username or password!";
        }
        // retrieve person by user id
        Person p = personRepository.findById(userId);
        // if person does not exist
        if (p == null) {
            return "No user found";
        }
        // get persons username
        String personUsername = p.getUsername();
        // if username and password match then set user as logged
        if(personUsername.equals(username) && pwEncoder.matches(password, p.getPassword())) {
                p.setLoggedIn(true);
                personRepository.save(p);
                return "You are logged in!";
            }
        return "Invalid stuff!";
    }

    /**
     * Logs a specific user in with password and email.
     *
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
     *
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
     * Updates password for a specific user with their email and new password.
     *
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