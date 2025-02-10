package com.coms309.duality.manageFriends;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserFriendsRequest {
    private List<String> friends;

}
