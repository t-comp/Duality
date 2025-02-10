package com.coms309.duality.controllers;

import com.coms309.duality.model.*;
import com.coms309.duality.repository.ActivityRepository;
import com.coms309.duality.repository.PersonRepository;
import com.coms309.duality.repository.PointsRepository;
//import com.coms309.duality.repository.YoutubeRepository;
//import com.coms309.duality.services.YoutubeVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Controls the activities in the application.
 * Includes CRUDL operations that can be performed by regular users, admin, and premium users.
 * @author Taylor Bauer
 */

@RestController
@RequestMapping("/activities")
public class ActivityController {

    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PointsRepository pointsRepository;

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

    /**
     * Checks if a category is valid based on previously defined activity categories.
     *
     * @param category - the category
     * @return if the category is valid or not
     */
    public boolean isValidCategory(String category) {
        ActivityCategory[] categories = ActivityCategory.values();
        for (ActivityCategory activityCategory : categories) {
            if (activityCategory.name().equalsIgnoreCase(category)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a new activity.
     *
     * @param newActivity - the activity to be created
     * @return the newly created activity
     */
    @PostMapping("/admin/create-activity/{userId}")
    public String createActivity(@RequestBody Activity newActivity, @PathVariable long userId) {
        Person p = personRepository.findById(userId);
        validAdmin(userId);
        if (!isValidCategory(newActivity.getCategory())) {
            return "Cannot use this category.";
        }
        if (activityRepository.findByActivityName(newActivity.getActivityName()).isEmpty()) {
            activityRepository.save(newActivity);
            return newActivity.getActivityName() + " has been created!";
        }
        return "Sorry, an activity with that name already exists!";
    }

    /**
     * Retrieves an activity by name. Used for search from user end.
     *
     * @param activityName - the specified activity
     * @param userId - id of the user
     * @return the specified activity
     */
    @GetMapping("/name")
    public List<Activity> getActivityByName(@RequestParam String activityName, @RequestParam long userId) {
        validUser(userId);
        return activityRepository.findByActivityName(activityName);
    }

    /**
     * Retrieves activity by category. If no category is given, all activities are shown.
     *
     * @param category - the specified category
     * @return the activities in a category or all activities if a category isn't given
     */
    @GetMapping("/category/")
    public List<Activity> getActivityByCategory(@RequestParam String category, @RequestParam long userId) {
        validUser(userId);
        if (!category.isEmpty()) {
            return activityRepository.findByCategory(category);
        }
        return activityRepository.findAll();
    }

    /**
     * Retrieves an activity by its ID - Admin usage.
     *
     * @param activityId - id of activity to be gotten.
     * @return the activity or null if not found
     */
    @GetMapping("/admin/{activityId}")
    public Activity getActivityById(@PathVariable long activityId, @RequestParam long userId) {
            validUser(userId);
            return activityRepository.findById(activityId).orElse(null);
    }

    /**
     * Updates an activity that has already been created. For admin usage.
     *
     * @param activityId - id of the activity to update
     * @param updatedActivity - info for the update of activity
     * @return the updated activity or null if not found
     */
    @PutMapping("/admin/update/{activityId}")
    public Activity updateActivity(@PathVariable long activityId, @RequestBody Activity updatedActivity, @RequestParam long userId) {
            validAdmin(userId);
            Activity a = activityRepository.findById(activityId).orElse(null);
            if (a == null) {
                return null;
            }
            // update activity
            a.setActivityName(updatedActivity.getActivityName());
            a.setDescription(updatedActivity.getDescription());
            a.setCategory(updatedActivity.getCategory());
            a.setLength(updatedActivity.getLength());
            a.setDescription(updatedActivity.getDescription());

            // save updated activity
            return activityRepository.save(a);
    }

    /**
     * Retrieves a specific user's saved activities.
     *
     * @param userId - the id of the user
     * @return list of activities saved by a user
     */
    @GetMapping("/{userId}/saved-activities")
    public List<Activity> getSavedActivities(@PathVariable Long userId) {
        Person p = personRepository.findById(userId).orElse(null);

        if (p != null && p.isLoggedIn()) {
            return p.getSavedActivities();
        }
        return new ArrayList<>();
    }


    /**
     * Deletes an activity by its ID and removes it from any users who have it as a saved activity.
     * Admin usage only.
     *
     * @param activityId - id of specified activity to be deleted
     * @return - success or failure
     */
    @DeleteMapping("admin/delete/{activityId}")
    public String deleteActivity(@PathVariable long activityId, @RequestParam long userId) {
        validAdmin(userId);
        if (activityRepository.existsById(activityId)) {
            Activity a = activityRepository.findById(activityId).orElse(null);
            if (a != null) {
                // find all users
                List<Person> everyone = personRepository.findAll();
                // remove the activity if saved
                for (Person p : everyone) {
                    List<Activity> savedActivities = p.getSavedActivities();
                    if (savedActivities.contains(a)) {
                        savedActivities.remove(a);
                        personRepository.save(p);
                    }
                }
                activityRepository.delete(a);
                return a.getActivityName() + " has been deleted.";
            }
        }
        return "Activity does not exist.";
    }

    /**
     * Deletes all activities and clears all saved activities from users. Admin usage only.
     *
     * @return success message saying that all activities have been deleted
     */
    @DeleteMapping("/admin/delete-all")
    public String deleteAllActivities(@RequestParam long userId) {
        validAdmin(userId);

        List<Person> everyone = personRepository.findAll();

        for (Person p : everyone) {
            // clear saved activities
            p.getSavedActivities().clear();
            personRepository.save(p);
        }
        activityRepository.deleteAll();

        return "All activities are gone.";
    }

    /**
     * Lists all activities that have been created.
     *
     * @return a list of all the activities.
     */
    @GetMapping("/all")
    public List<Activity> getAllActivities(@RequestParam long userId) {
        validUser(userId);
        return activityRepository.findAll();
    }

    @PostMapping("/complete/{userId}/{activityId}")
    public String completeActivity(@PathVariable long userId, @PathVariable long activityId) {
        Person p = personRepository.findById(userId);

        if (p != null && p.isLoggedIn()) {
            Activity a = activityRepository.findById(activityId).orElse(null);

            if (a == null) {
                return "Oh no! Error!";
            }
            // calc points
            int pointsEarned = calcActivityPoints(a);
            Points points = new Points(p, pointsEarned, HabitType.ACTIVITY);

            Integer currentPoints = p.getTotalPoints();
            if (currentPoints == null) {
                p.setTotalPoints(pointsEarned);
            } else {
                p.setTotalPoints(currentPoints + pointsEarned);
            }

            pointsRepository.save(points);
            personRepository.save(p);
            return "Activity completed!";
        }
        return "Error";
    }

    /**
     * Calculates the points for a user upon the completion of an activity.
     * @param a - the activity completed
     * @return  the number of points for completing an
     */
    private int calcActivityPoints(Activity a) {
        int points = 0;

        points += 300;

        return points;
    }

}