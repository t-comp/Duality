package com.coms309.duality.services.impl;

import com.coms309.duality.manageFriends.BlockUserRequest;
import com.coms309.duality.model.Person;
import com.coms309.duality.repository.UserFriendDAO;
import com.coms309.duality.services.BlockUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlockUserServiceImpl implements BlockUserService {

    @Autowired
    private UserFriendDAO userFriendDAO;

    @Override
    public String addBlockUser(BlockUserRequest blockUser) {
        //Map<String, Object> result = new HashMap<String, Object>();

        if(blockUser == null){
            return "Invalid Request";
        }

        if (blockUser.getRequestor() == null || blockUser.getTarget() == null) {
            return "Requester or Target can not be empty";
        }

        String email1 = blockUser.getRequestor();
        String email2 = blockUser.getTarget();

        if(email1.equals(email2)){
            return "Cannot Block Yourself";
        }

        Person p1 = this.userFriendDAO.findByEmail(email1);
        Person p2 = this.userFriendDAO.findByEmail(email2);

        p1.addBlockUsers(p2);
        this.userFriendDAO.save(p1);

        return "User Blocked";


    }
}
