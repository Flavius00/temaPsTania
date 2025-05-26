package com.example.demo.controller;

import com.example.demo.model.Notification;
import com.example.demo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Handles notifications sent from clients to /app/notification
     * Broadcasts the notification to all subscribers of /topic/public
     */
    @MessageMapping("/notification")
    @SendTo("/topic/public")
    public Notification broadcastNotification(Notification notification) {
        return notification;
    }

    /**
     * Sends a test notification to all connected clients
     */
    @MessageMapping("/test-notification")
    @SendTo("/topic/public")
    public Notification sendTestNotification() {
        return new Notification(
                "TEST",
                "This is a test notification",
                null,
                "all"
        );
    }
}