package com.appointment.service;

import com.appointment.domain.Appointment;
import com.appointment.domain.Administrator;
import com.appointment.domain.AppointmentType;
import com.appointment.domain.User;
import com.appointment.notifications.Observer;
import com.appointment.rules.BookingRuleStrategy;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Service class responsible for managing appointment bookings.
 * Uses LinkedList for in-memory storage and File for persistent storage.
 *
 * @author YourName
 * @version 1.0
 */
public class BookingService {

    /** File path for persistent storage */
    private static final String FILE_PATH = "appointments.txt";

    /** Observer for sending notifications */
    private final Observer observer;

    /** ScheduleService for freeing slots after cancellation */
    private final ScheduleService scheduleService;

    /** LinkedList of booking rules to validate appointments */
    private final List<BookingRuleStrategy> rules = new LinkedList<>();

    /** LinkedList of all booked appointments in memory */
    private final List<Appointment> appointments = new LinkedList<>();

    /**
     * Constructs BookingService with observer and scheduleService.
     * @param observer        the notification observer
     * @param scheduleService the schedule service for slot management
     */
    public BookingService(Observer observer, ScheduleService scheduleService) {
        this.observer = observer;
        this.scheduleService = scheduleService;
        loadFromFile();
    }

    /**
     * Adds a booking rule to the validation chain.
     * @param rule the BookingRuleStrategy to add
     */
    public void addRule(BookingRuleStrategy rule) {
        rules.add(rule);
    }

    /**
     * Books an appointment after validating all rules.
     * @param appointment the appointment to book
     * @return true if booking succeeded, false if any rule failed
     */
    public boolean book(Appointment appointment) {
        for (BookingRuleStrategy rule : rules) {
            if (!rule.isValid(appointment)) {
                return false;
            }
        }
        appointment.confirm();
        appointments.add(appointment);
        saveToFile(appointment);
        return true;
    }

    /**
     * Modifies an existing appointment by updating its date and time.
     * @param appointment the appointment to modify
     * @param newDate     the new date in YYYY-MM-DD format
     * @param newTime     the new time in HH:MM format
     * @return true if modification succeeded, false if appointment is in the past
     */
    public boolean modifyAppointment(Appointment appointment, String newDate, String newTime) {
        if (!appointment.isFuture()) {
            System.out.println("❌ Cannot modify past appointments");
            return false;
        }
        appointment.setDate(newDate);
        appointment.setTime(newTime);
        System.out.println("✅ Appointment modified");
        return true;
    }

    /**
     * Cancels an existing appointment by a user.
     * Frees the slot and sends cancellation email.
     *
     * @param date the date of appointment to cancel
     * @param time the time of appointment to cancel
     * @return true if cancellation succeeded, false otherwise
     */
    public boolean cancelAppointment(String date, String time) {
        for (Appointment a : appointments) {
            if (a.getDate().equals(date) && a.getTime().equals(time)) {
                if (!a.isFuture()) {
                    System.out.println("❌ Cannot cancel past appointments");
                    return false;
                }
                a.cancel();
                a.setParticipants(0);
                updateFile();

                // ✅ إرسال إيميل إلغاء
                observer.notify(a.getUser(),
                        "Your appointment on " + a.getDate() +
                                " at " + a.getTime() + " has been cancelled.");
                System.out.println("✅ Appointment cancelled");
                return true;
            }
        }
        System.out.println("❌ Appointment not found");
        return false;
    }

    /**
     * Cancels any appointment by an administrator.
     * Frees the slot and sends cancellation email.
     *
     * @param date  the date of appointment to cancel
     * @param time  the time of appointment to cancel
     * @param admin the administrator performing the cancellation
     * @return true if cancellation succeeded, false if admin is null
     */
    public boolean adminCancel(String date, String time, Administrator admin) {
        if (admin == null) {
            System.out.println("❌ Only admin can cancel");
            return false;
        }
        boolean found = false;
        for (Appointment a : appointments) {
            if (a.getDate().equals(date) && a.getTime().equals(time)
                    && !a.getStatus().equals("Cancelled")) {
                a.cancel();
                a.setParticipants(0);
                found = true;
            }
        }
        if (found) {
            updateFile();
            System.out.println("✅ Admin cancelled appointment");
            return true;
        }
        System.out.println("❌ Appointment not found");
        return false;
    }

    /**
     * Modifies any appointment by an administrator.
     *
     * @param appointment the appointment to modify
     * @param admin       the administrator performing the modification
     * @param newDate     the new date in YYYY-MM-DD format
     * @param newTime     the new time in HH:MM format
     * @return true if modification succeeded, false if admin is null
     */
    public boolean adminModify(Appointment appointment, Administrator admin,
                               String newDate, String newTime) {
        if (admin == null) {
            System.out.println("❌ Only admin can modify");
            return false;
        }
        appointment.setDate(newDate);
        appointment.setTime(newTime);
        System.out.println("✅ Admin modified appointment");
        return true;
    }

    /**
     * Saves appointment to file for persistent storage.
     * @param appointment the appointment to save
     */
    private void saveToFile(Appointment appointment) {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(FILE_PATH, true))) {
            writer.write(
                    appointment.getDate() + "," +
                            appointment.getTime() + "," +
                            appointment.getDuration() + "," +
                            appointment.getParticipants() + "," +
                            appointment.getUser().getUsername() + "," +
                            appointment.getType() + "," +
                            appointment.getStatus()
            );
            writer.newLine();
            System.out.println("💾 Appointment saved to file");
        } catch (IOException e) {
            System.out.println("❌ Failed to save appointment: " + e.getMessage());
        }
    }

    /**
     * Loads appointments from file into LinkedList on startup.
     */
    private void loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 7) {
                    User user = new User(parts[4], "0000");
                    Appointment a = new Appointment(
                            parts[0],
                            parts[1],
                            Integer.parseInt(parts[2]),
                            Integer.parseInt(parts[3]),
                            user,
                            AppointmentType.valueOf(parts[5])
                    );
                    appointments.add(a);
                }
            }
            if (!appointments.isEmpty()) {
                System.out.println("📂 Loaded " + appointments.size() + " appointments from file");
            }
        } catch (IOException e) {
            System.out.println("❌ Failed to load appointments: " + e.getMessage());
        }
    }

    /**
     * Updates file after cancellation.
     */
    private void updateFile() {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(FILE_PATH, false))) {
            for (Appointment a : appointments) {
                if (!a.getStatus().equals("Cancelled")) {
                    writer.write(
                            a.getDate() + "," +
                                    a.getTime() + "," +
                                    a.getDuration() + "," +
                                    a.getParticipants() + "," +
                                    a.getUser().getUsername() + "," +
                                    a.getType() + "," +
                                    a.getStatus()
                    );
                    writer.newLine();
                }
            }
            System.out.println("💾 File updated");
        } catch (IOException e) {
            System.out.println("❌ Failed to update file: " + e.getMessage());
        }
    }

    /**
     * Returns all appointments in memory.
     * @return LinkedList of appointments
     */
    public List<Appointment> getAppointments() {
        return appointments;
    }
}