package com.appointment.service;

import com.appointment.domain.Appointment;
import com.appointment.notifications.Observer;

public class ReminderService {

    private Observer observer;

    public ReminderService(Observer observer) {
        this.observer = observer;


    public void sendReminder(Appointment appointment) {

        String message = "Reminder: your appointment at " + appointment.getTime();

        observer.notify(appointment.getUser(), message);
    }
}