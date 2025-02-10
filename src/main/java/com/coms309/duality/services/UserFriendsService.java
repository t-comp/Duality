package com.coms309.duality.services;

import com.coms309.duality.manageFriends.UserFriendsListRequest;
import com.coms309.duality.manageFriends.UserFriendsRequest;

public interface UserFriendsService {

    String addUserFriends(UserFriendsRequest userFriendsRequest);

    String getUserFriends(UserFriendsListRequest userFriendsListRequest);

    String getCommonUserFriends(UserFriendsRequest userFriendsRequest);

    interface SubscriptionService {
        String createSubscription(long userId, String subscriptionId);
    }
}
