package com.coms309.duality.services.impl;

import com.coms309.duality.manageFriends.UserFriendsListRequest;
import com.coms309.duality.manageFriends.UserFriendsRequest;
import com.coms309.duality.model.Person;
import com.coms309.duality.repository.UserFriendDAO;
import com.coms309.duality.services.UserFriendsService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

@Service
public class UserFriendsServiceImpl implements UserFriendsService {

    @Autowired
    private UserFriendDAO userFriendDAO;

    private Person saveIfNotExist(String email){
        Person currentUser = this.userFriendDAO.findByEmail(email);
        if(currentUser == null){
            currentUser = new Person();
            currentUser.setEmail(email);
            return this.userFriendDAO.save(currentUser);
        }else{
            return currentUser;
        }
    }

    @Override
    public String addUserFriends(UserFriendsRequest userFriendsRequest){



        if (userFriendsRequest == null){

            return "User Not Found";
        }

        if(CollectionUtils.isEmpty(userFriendsRequest.getFriends())){
            return "Friends list does not exist";
        }

        if(userFriendsRequest.getFriends().size() != 2){
            return "You need to provide 2 emails to make them friends";
        }


        String email1 = userFriendsRequest.getFriends().get(0);
        String email2 = userFriendsRequest.getFriends().get(1);

        if(email1.equals(email2)){
            return "Cannot be Friends with self";
        }

        Person p1 = this.saveIfNotExist(email1);
        Person p2 = this.saveIfNotExist(email2);

        if(p1.getUserFriends().contains(p2)){
            return "You Are Already Friends";
        }

        if(p1.getBlockUsers().contains(p2)){
            return "You Have Blocked This User";

        }

        p1.addUserFriends(p2);
        this.userFriendDAO.save(p1);

        return "Friend added successfully";


    }

    @Override
    public String getUserFriends(UserFriendsListRequest userFriendsListRequest) {
       // Map<String, Object> result = new HashMap<String, Object>();

        if(userFriendsListRequest == null){
            return "INVALID REQUEST";

        }


        Person p = this.userFriendDAO.findByEmail(userFriendsListRequest.getEmail());
        List<String> friendsList = p.getUserFriends().stream().map(Person::getEmail).toList();

        return "Here is your friends list! \n Friends:" + friendsList + "Count: " + friendsList.size();

    }

    @Override
    public String getCommonUserFriends(UserFriendsRequest userFriendsRequest) {
        //Map<String, Object> result = new HashMap<String, Object>();

        if (userFriendsRequest == null){
            return "Invalid Request";

        }

        Person p1 = this.userFriendDAO.findByEmail(userFriendsRequest.getFriends().get(0));
        Person p2 = this.userFriendDAO.findByEmail(userFriendsRequest.getFriends().get(1));

        if(p1.getEmail().equals(p2.getEmail())){
            return "Users are same";
        }

        Set<Person> friends = p1.getUserFriends();
        friends.retainAll(p2.getUserFriends());

        return "Here is your Common Friends list! \n Friends:" + friends.stream().map(Person::getEmail).toList() + "Count: " + friends.size();




    }
}
