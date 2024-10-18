package com.coms309.duality.controllers;

import com.coms309.duality.model.Activity;
import com.coms309.duality.model.ActivityCategory;
import com.coms309.duality.model.Person;
import com.coms309.duality.repository.ActivityRepository;
import com.coms309.duality.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/activities")
public class ActivityController {

    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    private PersonRepository personRepository;


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
     * C: Creates a new activity.
     *
     * @param newActivity - the activity to be created
     * @return the newly created activity
     */
    @PostMapping("/admin/create-activity")
    public String createActivity(@RequestBody Activity newActivity) {
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
     * R: Retrieves an activity by name. Used for search from user end.
     *
     * @param activityName - the specified activity
     * @return the specified activity
     */
    @GetMapping("/name")
    public List<Activity> getActivityByName(@RequestParam String activityName) {
        return activityRepository.findByActivityName(activityName);
    }

    /**
     * Retrieves activity by category. If no category is given, all activities are shown.
     *
     * @param category - the specified category
     * @return the activities in a category or all activities if a category isn't given
     */
    @GetMapping("/category")
    public List<Activity> getActivityByCategory(@RequestParam String category) {
        if (!category.isEmpty()) {
            return activityRepository.findByCategory(category);
        }
        return activityRepository.findAll();
    }

    /**
     * R: Retrieves an activity by its ID - Admin usage.
     *
     * @param id - id of activity to be gotten.
     * @return the activity or null if not found
     */
    @GetMapping("/admin/{id}")
    public Activity getActivityById(@PathVariable long id) {
        Activity a = activityRepository.findById(id).orElse(null);
        return a;
    }

    /**
     * U: Updates an activity that has already been created. For admin usage.
     *
     * @param id              - id of the activity to update
     * @param updatedActivity - info for the update of activity
     * @return the updated activity or null if not found
     */
    @PutMapping("/admin/update/{id}")
    public Activity updateActivity(@PathVariable long id, @RequestBody Activity updatedActivity) {
        Activity a = activityRepository.findById(id).orElse(null);

        if (a == null) {
            return null;
        }

        // update activity
        a.setActivityName(updatedActivity.getActivityName());
        a.setDescription(updatedActivity.getDescription());
        a.setCategory(updatedActivity.getCategory());
        a.setLength(updatedActivity.getLength());

        // save updated activity
        return activityRepository.save(a);
    }

    /**
     * D: Deletes an activity by its ID and removes it from any users who have it as a saved activity.
     * Admin usage
     *
     * @param id - id of specified activity to be deleted
     * @return - success or failure
     */
    @DeleteMapping("admin/delete/{id}")
    public String deleteActivity(@PathVariable long id) {
        if (activityRepository.existsById(id)) {
            Activity a = activityRepository.findById(id).orElse(null);
            if (a != null) {
                // find all users
                List<Person> users = personRepository.findAll();

                // remove the activity if saved
                for (Person p : users) {
                    List<Activity> savedActivities = p.getSavedActivities();
                    if (savedActivities.contains(a)) {
                        savedActivities.remove(a);
                        personRepository.save(p);
                    }
                }
                activityRepository.delete(a);
                return "Activity has been deleted.";
            }
        }
        return "Activity does not exist.";
    }

    /**
     * D: Deletes all activities and clears all saved activities from users. For admin usage.
     *
     * @return success message saying that all activities have been deleted
     */
    @DeleteMapping("/admin/delete-all")
    public String deleteAllActivities() {
        List<Person> users = personRepository.findAll();

        for (Person p : users) {
            // clear saved activities
            p.getSavedActivities().clear();
            personRepository.save(p);
        }
        activityRepository.deleteAll();

        return "All activities are gone.";
    }

    /**
     * L: Lists all activities that have been created.
     *
     * @return a list of all the activities.
     */
    @GetMapping("/all")
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

}