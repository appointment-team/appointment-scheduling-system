package com.appointment.service;

import com.appointment.domain.AppointmentSlot;
import java.util.Collections;
import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Service for managing appointment slots.
 * Uses LinkedList for in-memory storage and File for persistent storage.
 * @author YourName
 * @version 1.0
 */
public class ScheduleService {

    private static final String FILE_PATH = "slots.txt";
    private List<AppointmentSlot> slots = new LinkedList<>();

    /**
     * Constructs ScheduleService and loads slots from file.
     * If no slots found, adds default slots.
     */
    public ScheduleService() {
        loadFromFile();
        if (slots.isEmpty()) {
            slots.add(new AppointmentSlot("10:00"));
            slots.add(new AppointmentSlot("11:00"));
            slots.add(new AppointmentSlot("12:00"));
            saveToFile();
        }
    }

    /**
     * Returns list of available (not booked) slots.
     * @return list of available slots
     */
    public List<AppointmentSlot> getAvailableSlots() {
        List<AppointmentSlot> available = new LinkedList<>();
        for (AppointmentSlot slot : slots) {
            if (!slot.isBooked()) {
                available.add(slot);
            }
        }
        return available;
    }

    /**
     * Adds a new slot and saves to file.
     * @param time the time of the slot in HH:MM format
     */
    public void addSlot(String time) {
        for (AppointmentSlot slot : slots) {
            if (slot.getTime().equals(time)) {
                System.out.println("❌ Slot already exists: " + time);
                return;
            }
        }
        slots.add(new AppointmentSlot(time));
        // ✅ رتّب بعد الإضافة
        slots.sort((a, b) -> a.getTime().compareTo(b.getTime()));
        saveToFile();
        System.out.println("✅ Slot added: " + time);
    }

    /**
     * Saves all slots to file.
     */
    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(FILE_PATH, false))) {
            for (AppointmentSlot slot : slots) {
                writer.write(slot.getTime() + "," + slot.isBooked());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("❌ Failed to save slots: " + e.getMessage());
        }
    }

    /**
     * Loads slots from file on startup.
     */
    private void loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(
                new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    AppointmentSlot slot = new AppointmentSlot(parts[0]);
                    if (parts[1].equals("true")) {
                        slot.book();
                    }
                    slots.add(slot);
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Failed to load slots: " + e.getMessage());
        }
    }
    public void freeSlot(String time) {
        for (AppointmentSlot slot : slots) {
            if (slot.getTime().equals(time) && slot.isBooked()) {
                slot.free();
                saveToFile();
                System.out.println("✅ Slot " + time + " is now available");
                return;
            }
        }
    }
}