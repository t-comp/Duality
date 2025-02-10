package com.coms309.duality.controllers;

import com.coms309.duality.model.*;
import com.coms309.duality.repository.CheckinRepository;
import com.coms309.duality.repository.PersonRepository;
import com.coms309.duality.repository.PointsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Controller for handling check-in tasks. Includes CRUDL operations.
 * @author Taylor Bauer
 */
@RestController
@RequestMapping("/checkin")
public class CheckinController {

    @Autowired
    private CheckinRepository checkinRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PointsRepository pointsRepository;

    /**
     * Creates a new checkin entry for a user.
     *
     * @param userId - the id of the user checking in
     * @param newCheckin - the checkin data
     * @return a message indicating the outcome of the operation
     */
    @PostMapping("create/{userId}")
    public String createCheckin(@PathVariable long userId, @RequestBody Checkin newCheckin) {
        Person p = personRepository.findById(userId);

        if (p != null && p.isLoggedIn()) {
            List<Checkin> todaysCheckin = checkinRepository.findByPersonAndDate(p, LocalDate.now());
            // user cannot create more than one checkin per day
            if (!todaysCheckin.isEmpty()) {
                return "Sorry! No can do, you already have a checkin from today.";
            }
            // set person and checkin date
            newCheckin.setPerson(p);
            newCheckin.setDate(LocalDate.now());

            // randomly generated steps for checkin
            Random random  = new Random();
            int randomSteps = random.nextInt(9000) + 3000;
            newCheckin.setSteps(randomSteps);

            // update user streaks
            int streak = getCheckinStreak(userId);
            newCheckin.setStreak(streak + 1);

            // calculate and set points for checkin
            int pointsEarned = calcCheckinPoints(newCheckin);
            Points points = new Points(p, pointsEarned, HabitType.CHECKIN);
            Integer currentPoints = p.getTotalPoints();

            if (currentPoints == null) {
                p.setTotalPoints(pointsEarned);
            } else {
                p.setTotalPoints(currentPoints + pointsEarned);
            }
            // add checkin and save
            p.getPersonCheckins().add(newCheckin);
            checkinRepository.save(newCheckin);
            pointsRepository.save(points);
            personRepository.save(p);

            return "Checkin created! You earned " + pointsEarned + " points :)";
        }
        return "Error";

    }

    /**
     * Retrieves current checkin streak for a user.
     *
     * @param userId - the id of the user
     * @return the current streak
     */
    @GetMapping("/streak/{userId}")
    public int getCheckinStreak(@PathVariable long userId) {
        Person p = personRepository.findById(userId);

        if (p != null) {
            List<Checkin> pastCheckins = checkinRepository.findByPersonOrderByDateDesc(p);
            if (pastCheckins.isEmpty()) {
                return 0;
            }
            int streak = 0;
            LocalDate date = LocalDate.now();
            for (Checkin c: pastCheckins) {
                if (c.getDate().equals(date) || c.getDate().equals(date.minusDays(1))) {
                    streak++;
                    date = c.getDate();
                } else {
                    break;
                }
            }
            return streak;
        }
        return 0;
    }

    /**
     * Retrieves checkin from the past seven days for a user. Available to both premium and regular users.
     *
     * @param userId - the id of the user
     * @return a list of checkins from the past seven days
     */
    @GetMapping("/sevenDays/{userId}")
    public List<Checkin> previousSevenCheckins(@PathVariable long userId) {
        Person p = personRepository.findById(userId);

        if (p != null && p.isLoggedIn()) {
            // get checkins from past seven days
            LocalDate sevenDays = LocalDate.now().minusDays(7);
            return checkinRepository.findByPersonAndDateAfterOrderByDateDesc(p, sevenDays);
        }
        return null;
    }

    /**
     * Retrieves a checkin for a user from a specific date. Premium usage only.
     *
     * @param userId - the id of the user
     * @param date - the data of the checkin
     * @return the checkin from the specified date
     */
    @GetMapping("/{date}/{userId}")
    public List<Checkin> getCheckin(@PathVariable long userId, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Person p = personRepository.findById(userId);
        // only for logged in premium or admins users
        if (p != null && p.isLoggedIn()) {
            if (p.getType() == UserType.PREMIUM || p.getType() == UserType.ADMIN) {
                // get checkin by date
                List<Checkin> c = checkinRepository.findByPersonAndDate(p, date);
                if (c.isEmpty()) {
                    System.out.println("You did not have a check-in from today!");
                }
                return c;
            }
        }
        return new ArrayList<>();
    }

    /**
     * Updates an existing checkin.
     *
     * @param checkinId - the id of the checkin
     * @param updatedCheckin - the updated checkin data
     * @param userId - the id of the user
     * @return a message indicating a result of the operation
     */
    @PutMapping("/edit/{userId}/{checkinId}")
    public String updateCheckin(@PathVariable long checkinId, @RequestBody Checkin updatedCheckin, @PathVariable long userId) {
        Person p = personRepository.findById(userId);
        if (p != null && p.isLoggedIn()) {
            Checkin c = checkinRepository.findById(checkinId).orElse(null);
            if (c == null) {
                return "Checkin not found.";
            }

            if (p.getType() == UserType.ADMIN || c.getPerson().getUserID() == userId) {
                // update
                c.setMentalHealthRating(updatedCheckin.getMentalHealthRating());
                c.setPhysicalHealthRating(updatedCheckin.getPhysicalHealthRating());
                c.setWaterAmount(updatedCheckin.getWaterAmount());
                c.setNotes(updatedCheckin.getNotes());

                // save updated checkin
                checkinRepository.save(c);

                return "Checkin updated!";
            }
        }
        return "Error.";
    }

    /**
     * Deletes a specific checkin entry.
     *
     * @param checkinId - the id of the checkin
     * @param userId - the id of the user
     * @return a message indicating outcome of the operation
     */
    @DeleteMapping("/delete/{checkinId}/{userId}")
    public String deleteCheckin(@PathVariable long checkinId, @PathVariable long userId) {
        Person p = personRepository.findById(userId);
        if (p != null && p.isLoggedIn()) {
            Checkin c = checkinRepository.findById(checkinId).orElse(null);
            if (c == null) {
                return "No checkin to delete!";
            }
            if (p.getType() == UserType.ADMIN || c.getPerson().getUserID() == userId) {
                c.getPerson().getPersonCheckins().remove(c);
                checkinRepository.delete(c);
                return "Checkin deleted!";
            }
        }
        return "Error!";
    }

    /**
     * Retrieves all checkins for a specific user. Only available to admin.
     *
     * @param userId - the id of the user
     * @return a list of all the checkins
     */
    @GetMapping("/{userId}")
    public List<Checkin> getAllCheckins(@PathVariable long userId) {
        Person p = personRepository.findById(userId);
        if (p != null && p.isLoggedIn() && p.getType() == UserType.ADMIN) {
            return checkinRepository.findByPersonOrderByDateDesc(p);
        }
        return null;
    }

    /**
     * Deletes all checkins in the repository. Only available to admin.
     *
     * @return a message stating that all checkins have been deleted
     */
    @DeleteMapping("/delete-all")
    public String deleteAllCheckins(@RequestParam long userId) {
        Person p  = personRepository.findById(userId);
        if (p != null && p.isLoggedIn() && p.getType() == UserType.ADMIN) {
            checkinRepository.deleteAll();
            return "All checkins deleted!";
        }
        return "Oh no! There is something here you aren't allowed to do";
    }

    /**
     * Calculates checkin points a user receives upon completing a checkin.
     * @param checkin - the checkin completed
     * @return the number of points accumulated
     */
    private int calcCheckinPoints(Checkin checkin) {
        int points = 0;

        // creating a checkin
        points += 100;
        // number of steps
        if (checkin.getSteps() >= 10000) {
            points += 300;
        }
        // amount of water
        if (checkin.getWaterAmount() >= 2200) {
            points += 200;
        }

        // checkin streak
        int streak = getCheckinStreak(checkin.getPerson().getUserID());

        if (streak >= 7) {
            points += 400;
        } else if (streak >= 3) {
            points += 250;
        }

        // hours of sleep
        if (checkin.getSleep() >= 7 && checkin.getSleep() <= 11) {
            points += 350;
        }

        return points;
    }

    // removes dupe checkins automatically

    /**
     * Removes duplicate checkins at midnight every night. Utility method.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void removeDupeCheckins() {
        List<Person> everyone = personRepository.findAll();

        for (Person p: everyone) {
            List<Checkin> checkins = checkinRepository.findByPersonOrderByDateDesc(p);
            LocalDate date = null;
            List<Checkin> toRemove = new ArrayList<>();

            for (Checkin c : checkins) {
                if (c.getDate().equals(date)) {
                    toRemove.add(c);
                } else {
                    date = c.getDate();
                }
            }
            for (Checkin duplicateCheckin : toRemove) {
                p.getPersonCheckins().remove(duplicateCheckin);
                checkinRepository.delete(duplicateCheckin);
            }
            if (!toRemove.isEmpty()) {
                personRepository.save(p);
            }
        }
    }

    /**
     * Manually removes all duplicate checkins.
     * @return a success message
     */
    @PostMapping("/remove-dupes")
    public String manualRemoveDupeCheckins() {
        removeDupeCheckins();
        return "All done!";
    }
}