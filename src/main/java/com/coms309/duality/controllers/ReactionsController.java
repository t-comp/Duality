package com.coms309.duality.controllers;

import com.coms309.duality.model.Activity;
import com.coms309.duality.model.Reaction;
import com.coms309.duality.services.ReactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

//doo doo fart
@Controller
public class ReactionsController {


    @Autowired
    private ReactionService reactionService;

    @PutMapping("/react")
    public ResponseEntity<?> reactToActivity(@RequestParam long senderId, long receiverId,
                                             @RequestParam String emoji, @RequestParam long activityId) {
        try {
            // Send reaction to the activity creator
            reactionService.verifyFriendship(senderId, receiverId, emoji, activityId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
