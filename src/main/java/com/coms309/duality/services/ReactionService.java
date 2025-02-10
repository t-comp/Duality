package com.coms309.duality.services;


import com.coms309.duality.model.Person;
import com.coms309.duality.model.Reaction;
import com.coms309.duality.repository.PersonRepository;
import com.coms309.duality.repository.ReactionRepository;
import com.coms309.duality.repository.UserFriendDAO;
import com.coms309.duality.websockets.ReactionServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReactionService {

    @Autowired
    private ReactionServer reactionServer;
    @Autowired
    private ReactionRepository reactionRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private UserFriendDAO userFriendDAO;

    private SimpMessagingTemplate messagingTemplate;

    public void verifyFriendship(long senderId, long receiverId, String emoji, long activityId) {
        Person sender = personRepository.findById(senderId);
        Person receiver = personRepository.findById(receiverId);


        if (isFriends(sender, receiver)) {
            // Create and save newReaction
            Reaction newReaction = new Reaction();
            newReaction.setUserId(senderId);
            newReaction.setActivityId(activityId);
            newReaction.setEmoji(emoji);
            newReaction.setActivityCreatorId(String.valueOf(receiverId));

            saveReaction(newReaction);

            sendReactionNotification(sender, receiver, newReaction);
        }else{
            throw new RuntimeException("Users are not friends");
        }

    }

    private void sendReactionNotification(Person sender, Person receiver, Reaction reaction) {
        try{
            messagingTemplate.convertAndSend(
            "/topic/reactions" + receiver.getUserID(),
                    new Reaction(reaction.getUserId(), reaction.getActivityId(),
                            reaction.getEmoji(), String.valueOf(sender.getUserID()))
            );
            messagingTemplate.convertAndSend(
                    "/topic/reactions" + sender.getUserID(),
                    new Reaction(reaction.getUserId(), reaction.getActivityId(),
                            reaction.getEmoji(), String.valueOf(receiver.getUserID()))
            );
        }catch (Exception e){
            System.err.println("Error sending reaction notification: " + e.getMessage());
        }
    }
    private boolean isFriends(Person sender, Person receiver) {
        return sender.getUserFriends().contains(receiver)
                && receiver.getUserFriends().contains(sender);
    }

        public void saveReaction (Reaction newReaction){

            reactionRepository.save(newReaction);
        }
    }

