package com.appointment.notifications;

import com.appointment.domain.User;

public class NotificationService implements Observer {

    @Override
    public void notify(User user, String message) {

        System.out.println("Notification sent to " + user.getUsername());
        System.out.println("Message: " + message);

    }

}