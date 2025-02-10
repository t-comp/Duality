package com.coms309.duality.controllers;

import com.coms309.duality.manageFriends.BlockUserRequest;
import com.coms309.duality.manageFriends.UserFriendsListRequest;
import com.coms309.duality.manageFriends.UserFriendsRequest;
import com.coms309.duality.services.BlockUserService;
import com.coms309.duality.services.UserFriendsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/friendapi")
public class FriendApiController {

    @Autowired
    private UserFriendsService userFriendsService;

    @Autowired
    private BlockUserService blockUserService;

    @PostMapping("/friendRequest")
    public String userFriendRequest(@RequestBody UserFriendsRequest userFriendsRequest) {
        return this.userFriendsService.addUserFriends(userFriendsRequest);
    }

    @PostMapping("/friendsList")
    public String getUserFriendList(@RequestBody UserFriendsListRequest userFriendsListRequest) {
        return this.userFriendsService.getUserFriends(userFriendsListRequest);
    }

    @PostMapping("/CommonUserFriends")
    public String getCommonUserFriends(@RequestBody UserFriendsRequest userFriendsRequest) {
        return this.userFriendsService.getCommonUserFriends(userFriendsRequest);
    }

    @PostMapping("/blockUserRequest")
    public String blockUserRequest(@RequestBody BlockUserRequest blockUserRequest) {
        return this.blockUserService.addBlockUser(blockUserRequest);
    }

}
