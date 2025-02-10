package com.coms309.duality.websockets;

import com.coms309.duality.model.Person;
import com.coms309.duality.repository.PersonRepository;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@ServerEndpoint("/notification/{username}")
public class NotificationServer {

    @Setter
    private static PersonRepository personRepository;


    private static final Logger logger = LoggerFactory.getLogger(NotificationServer.class);
    private static final Map<String, Session> userSessions = new HashMap<>();
    private static final Set<String> subscribedUsers = new HashSet<>();


    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException {
        Person p = personRepository.findByUsername(username);
        if (p != null) {
            synchronized (userSessions) {
                userSessions.put(username, session);
            }
            logger.info("[onOpen] '{}' connected", username);
            session.getBasicRemote().sendText("Connected. Please type 'subscribe' to get notifications.");
        } else {
            logger.warn("User '{}' not found, closing session", username);
            session.close();
        }
    }


    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        synchronized (userSessions) {
            userSessions.remove(username);
            unsubscribeUser(username);
        }
        logger.info("[onClose] '{}' disconnected", username);
    }
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("username") String username) {
        if ("subscribe".equalsIgnoreCase(message)) {
            subscribeUser(username);
            try {
                session.getBasicRemote().sendText("You have subscribed for notifications.");
            } catch (IOException e) {
                logger.error("Error sending subscription confirmation: {}", e.getMessage());
            }
        }
    }
    public void subscribeUser(String username) {
        synchronized (subscribedUsers) {
            subscribedUsers.add(username);
        }
        logger.info("'{}' has subscribed for notifications!", username);
    }
    public void unsubscribeUser(String username) {
        synchronized (subscribedUsers) {
            subscribedUsers.remove(username);
        }
        logger.info("'{}' has unsubscribed from notifications :(", username);
    }

    public boolean isSubscribed(String username) {
        return subscribedUsers.contains(username);
    }

    public void broadcastMessage(String username, String message) {
        synchronized (userSessions) {
            for (Map.Entry<String, Session> entry : userSessions.entrySet()) {
                String targetUser = entry.getKey();
                if (subscribedUsers.contains(targetUser)) {
                    try {
                        Session session = entry.getValue();
                        session.getBasicRemote().sendText(username + ": " + message);
                    } catch (IOException e) {
                        logger.error("Error sending message to {}: {}", targetUser, e.getMessage());
                    }
                }
            }
        }
    }
}
