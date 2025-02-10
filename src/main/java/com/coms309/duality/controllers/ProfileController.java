package com.coms309.duality.controllers;

import com.coms309.duality.manageFriends.UserFriendsListRequest;
import com.coms309.duality.model.Item;
import com.coms309.duality.model.Person;
import com.coms309.duality.model.Profile;
import com.coms309.duality.model.UserType;
import com.coms309.duality.model.UserItems;
import com.coms309.duality.repository.PersonRepository;
import com.coms309.duality.repository.ProfileRepository;
import com.coms309.duality.repository.UserFriendDAO;
import com.coms309.duality.services.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private UserFriendDAO userFriendDAO;

    @Autowired
    private ProfileService profileService;


    boolean isPremium(Person p) {
        return p != null && p.getType() == UserType.PREMIUM;
    }



    @GetMapping("/{userId}")
    public Profile getProfile(@PathVariable long userId) {
        Person p = personRepository.findById(userId);
        if(p!=null){

             return p.getUserProfile();
        }
        return null;

    }


    @PutMapping("/{userId}/updateProfile")
    public String updateProfile(@PathVariable long userId, @RequestBody Profile updatedProfile) {
        Person user = personRepository.findById(userId);

        if (user == null || user.getUserID() != userId) {
            return "There is no profile here!";
        }

        //get user profile and add updates
        Profile p = user.getUserProfile();
        p.setName(user.getFirstName(), user.getLastName());
        p.setEmail(user.getEmail());
        p.setPronouns(updatedProfile.getPronouns());
        p.setBio(updatedProfile.getBio());
        p.setDob(updatedProfile.getDob());
        p.setTotalPoints(user.getTotalPoints());

        //save updated profile
        profileRepository.save(p);

        return "Updated Profile Saved!";
    }
// Not necessary. When a user is deleted, the profile is deleted
//    @DeleteMapping("/{profileId}")
//    public String deleteProfile(@PathVariable long profileId) {
//        Profile p1 = profileRepository.findById(profileId);
//        if (profileRepository.existsById(profileId)) {
//           profileRepository.delete(p1);
//            return "Profile has been deleted";
//        }
//
//        return "Profile does not exist";
//    }

    @GetMapping("/{userId}/friendsList")
    public List<String> getFriends(@PathVariable long userId) {
        UserFriendsListRequest userFriendsListRequest = new UserFriendsListRequest();
        Person p = personRepository.findById(userId);
        String email = p.getEmail();

        return p.getUserFriends().stream().map(Person::getEmail).toList();
    }

    @PostMapping("/{profileId}/photo")
    public ResponseEntity<String> uploadProfilePhoto(
            @PathVariable Long profileId,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        String photoUrl = profileService.uploadProfilePhoto(file, profileId);
        return ResponseEntity.ok(photoUrl);
    }

    @GetMapping("/items/{userId}")
    public List<Item> getUserItems(@PathVariable long userId) {
        Person p = personRepository.findById(userId);
        if(p==null){
            return null;
        }
        return p.getOwnedItems().stream()
                .map(UserItems::getItem)
                .collect(Collectors.toList());
    }




}
