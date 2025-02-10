package com.coms309.duality.websockets;


import com.coms309.duality.model.Person;
import com.coms309.duality.repository.PersonRepository;
import com.coms309.duality.repository.UserFriendDAO;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;


@Component
@ServerEndpoint("/reaction/{userId}")
public class ReactionServer {

    @Autowired
    private PersonRepository personRepository;
    @Getter
    private final UserFriendDAO userFriendDAO;


    public ReactionServer(UserFriendDAO userFriendDAO) {
        this.userFriendDAO = userFriendDAO;
    }


    public void setReactionRepository(PersonRepository person) {
        personRepository = person;
    }

    private final ConcurrentHashMap<Long, Session> senderSessionMap = new ConcurrentHashMap<>();
    private final CopyOnWriteArraySet<Long> sender = new CopyOnWriteArraySet<>();


    private final Logger logger = LoggerFactory.getLogger(ReactionServer.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("senderId") long senderId) throws IOException {

        Person p = personRepository.findById(senderId);

        senderSessionMap.put(senderId, session);

        logger.info("[onOpen] '{}' connected", p.getUsername());
        sendWelcomeMessage(session);
    }

    private void sendWelcomeMessage(Session session) throws IOException {
        session.getBasicRemote().sendText("Connected. Ready to send reactions.");
    }

    /**
     * Handles incoming WebSocket messages from a client.
     *
     * @param session  The WebSocket session representing the client's connection.
     * @param reaction The message received from the client.
     */
    @OnMessage
    public void onMessage(Session session, String reaction, @PathParam("senderId") long senderId, @PathParam("receiverId") long receiverId) throws IOException {

        Person sender = personRepository.findById(senderId);
        senderSessionMap.put(senderId, session);

        // Direct message to a user using the format "@username <message>"
        if ("reaction".equalsIgnoreCase(reaction)) {
            registerSender(senderId);
            session.getBasicRemote().sendText("You are now connected. Type reaction to be sent");

        }


    }

    private void registerSender(long userId) {
        Person person = personRepository.findById(userId);

        sender.add(userId);
        logger.info("'{}' can now send a reaction!", person.getUsername());
    }


    public void disconnectSender(long userId) {
        Person p = personRepository.findById(userId);
        sender.remove(userId);
        senderSessionMap.remove(userId);

        logger.info("Disconnected: '{}'",
                p != null ? p.getUsername() : "Unknown User");
    }


    public boolean isSender(long userId) {
        return sender.contains(userId);
    }

    /**
     * Handles the closure of a WebSocket connection.
     *
     * @param session The WebSocket session that is being closed.
     */
    @OnClose
    public void onClose(Session session, @PathParam("senderId") long userId) throws IOException {

        // get the username from session-username mapping

        Person p = personRepository.findById(userId);

        sender.remove(userId);
        senderSessionMap.remove(userId);

        logger.info("[onClose] '{}' disconnected",
                p != null ? p.getUsername() : "Unknown User");


    }


    /**
     * Sends a message to a specific user in the chat (DM).
     * []nmj'
     *
     * @param receiverId the recipient
     * @param reaction   message to be sent.
     */
    public void sendMessageToFriend(long receiverId, String reaction) {

        Session receiverSession = senderSessionMap.get(receiverId);
        if (receiverSession != null && receiverSession.isOpen()) {
            try {
                receiverSession.getBasicRemote().sendText(reaction);
            } catch (IOException e) {
                logger.error("Failed to send reaction to receiver", e);
            }
        } else {
            logger.warn("No active session for receiver: {}", receiverId);
        }

    }
    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("WebSocket error", throwable);
        try {
            session.close();
        } catch (IOException e) {
            logger.error("Error closing session after error", e);
        }
    }



}
