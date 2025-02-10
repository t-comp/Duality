package com.coms309.duality.controllers;

import com.coms309.duality.services.CheckinNotifsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for notification tasks such as sending scheduled and direct notifs to users.
 *
 * @author Taylor Bauer
 */

@RestController
@RequestMapping("/notifications")
public class NotificationsController {

    @Autowired
    private CheckinNotifsService checkinNotifsService;

    /**
     * Sends scheduled notification.
     *
     * @return a success message
     */
    @PostMapping("/send-scheduled")
    public ResponseEntity<String> sendScheduledNotifications() {
        checkinNotifsService.sendScheduledNotifications();
        return ResponseEntity.ok("Scheduled notifications!");
    }

    /**
     * Sends notification to user by their id.
     *
     * @param userId - id of the user to get notification
     * @param message - the message to send to user
     * @return a success message
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestParam long userId, @RequestParam String message) {
        checkinNotifsService.notifyUserById(userId, message);
        return ResponseEntity.ok("Notification sent!");
    }
}
