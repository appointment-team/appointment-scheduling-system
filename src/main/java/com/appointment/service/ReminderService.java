package com.appointment.service;

import com.appointment.domain.Appointment;
import com.appointment.notifications.Observer;

/**
 * Service for sending appointment reminders.
 * @author YourName
 * @version 1.0
 */
public class ReminderService {

    private Observer observer;

    /**
     * Constructs ReminderService with an observer.
     * @param observer the notification observer
     */
    public ReminderService(Observer observer) {
        this.observer = observer;
    }

    /**
     * Sends a reminder for an upcoming appointment.
     * @param appointment the appointment to remind about
     */
    public void sendReminder(Appointment appointment) {
        String message = "Reminder: your appointment is on " +
                appointment.getDate() +
                " at " + appointment.getTime() +
                " | Type: " + appointment.getType() +
                " | Duration: " + appointment.getDuration() + " hour(s)";

        observer.notify(appointment.getUser(), message);
    }
}