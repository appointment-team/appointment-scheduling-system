package com.appointment.notifications;
import com.appointment.domain.User;

public interface Observer {


        void notify(User user, String message);

    }


















