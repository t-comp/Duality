package com.coms309.duality.services;

import com.coms309.duality.model.Person;
import com.coms309.duality.repository.PersonRepository;
import com.coms309.duality.websockets.NotificationServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CheckinNotifsService {

    @Autowired
    private NotificationServer notificationServer;
    @Autowired
    private PersonRepository personRepository;



    public void notifyUserById(long userId, String message) {
        Person person = personRepository.findById(userId);
        if (person != null) {
            String username = person.getUsername();
            notificationServer.broadcastMessage(username, message);
        }
    }
    // second, hour, minute, day of month, month, day of week
//    @Scheduled(cron = "0 15 16 * * ?")
    @Scheduled(fixedRate = 10000)
    public void sendScheduledNotifications() {
        List<Person> people = personRepository.findAll();
        String message = "Hey there! It's time for a checkin :)!";
        for (Person p : people) {
            String username = p.getUsername();
            if (notificationServer.isSubscribed(username)) {
                notificationServer.broadcastMessage(username, message);
            }
        }
    }
}
