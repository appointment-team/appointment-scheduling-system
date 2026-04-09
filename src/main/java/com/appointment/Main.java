package com.appointment;

import com.appointment.domain.*;
import com.appointment.notifications.NotificationService;
import com.appointment.notifications.Observer;
import com.appointment.service.*;

import java.util.List;
import java.util.Scanner;

/**
 * Main entry point for the Appointment Scheduling System.
 * @author YourName
 * @version 1.0
 */
public class Main {

    static AuthService authService = new AuthService();
    static BookingService bookingService = new BookingService();
    static ScheduleService scheduleService = new ScheduleService();
    static Observer observer = new NotificationService();
    static ReminderService reminderService = new ReminderService(observer);
    static Scanner scanner = new Scanner(System.in);
    static User currentUser = null;

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("   Appointment Scheduling System       ");
        System.out.println("========================================");

        boolean running = true;

        while (running) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Login as Administrator");
            System.out.println("2. Continue as User");
            System.out.println("3. Exit");
            System.out.print("Choose: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> adminMenu();
                case "2" -> userMenu();
                case "3" -> {
                    System.out.println("Goodbye!");
                    running = false;
                }
                default -> System.out.println("❌ Invalid choice");
            }
        }
    }

    // =========================
    // ADMIN MENU
    // =========================
    static void adminMenu() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (!authService.login(username, password)) {
            System.out.println("❌ Invalid credentials");
            return;
        }

        System.out.println("✅ Admin logged in!");

        boolean running = true;
        while (running) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. View available slots");
            System.out.println("2. Cancel any appointment");
            System.out.println("3. Logout");
            System.out.print("Choose: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> viewSlots();
                case "2" -> adminCancel();
                case "3" -> {
                    authService.logout();
                    System.out.println("✅ Logged out");
                    running = false;
                }
                default -> System.out.println("❌ Invalid choice");
            }
        }
    }

    // =========================
    // USER MENU
    // =========================
    static void userMenu() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        currentUser = new User(name, "0000");

        boolean running = true;
        while (running) {
            System.out.println("\n--- User Menu ---");
            System.out.println("1. View available slots");
            System.out.println("2. Book appointment");
            System.out.println("3. Cancel appointment");
            System.out.println("4. Back");
            System.out.print("Choose: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> viewSlots();
                case "2" -> bookAppointment();
                case "3" -> cancelAppointment();
                case "4" -> running = false;
                default -> System.out.println("❌ Invalid choice");
            }
        }
    }

    // =========================
    // VIEW SLOTS
    // =========================
    static void viewSlots() {
        List<AppointmentSlot> slots = scheduleService.getAvailableSlots();
        if (slots.isEmpty()) {
            System.out.println("❌ No available slots");
            return;
        }
        System.out.println("\n--- Available Slots ---");
        for (int i = 0; i < slots.size(); i++) {
            System.out.println((i + 1) + ". " + slots.get(i).getTime());
        }
    }

    // =========================
    // BOOK APPOINTMENT
    // =========================
    static void bookAppointment() {
        System.out.print("Date (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        System.out.print("Time (HH:MM): ");
        String time = scanner.nextLine();
        System.out.print("Duration (1 or 2 hours): ");
        int duration = Integer.parseInt(scanner.nextLine());
        System.out.print("Participants: ");
        int participants = Integer.parseInt(scanner.nextLine());

        System.out.println("Type: 1.URGENT 2.FOLLOW_UP 3.VIRTUAL 4.IN_PERSON 5.INDIVIDUAL 6.GROUP 7.ASSESSMENT");
        System.out.print("Choose type: ");
        int typeChoice = Integer.parseInt(scanner.nextLine());

        AppointmentType type = switch (typeChoice) {
            case 1 -> AppointmentType.URGENT;
            case 2 -> AppointmentType.FOLLOW_UP;
            case 3 -> AppointmentType.VIRTUAL;
            case 4 -> AppointmentType.IN_PERSON;
            case 5 -> AppointmentType.INDIVIDUAL;
            case 6 -> AppointmentType.GROUP;
            default -> AppointmentType.ASSESSMENT;
        };

        Appointment appointment = new Appointment(date, time, duration, participants, currentUser, type);
        AppointmentValidator validator = new AppointmentValidator();

        if (!validator.validate(appointment)) {
            System.out.println("❌ Appointment not valid");
            return;
        }

        bookingService.book(appointment);
        reminderService.sendReminder(appointment);
        System.out.println("✅ Appointment booked: " + date + " at " + time);
    }

    // =========================
    // CANCEL APPOINTMENT
    // =========================
    static void cancelAppointment() {
        System.out.print("Date of appointment to cancel (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        System.out.print("Time (HH:MM): ");
        String time = scanner.nextLine();

        Appointment appointment = new Appointment(date, time, 1, 1, currentUser, AppointmentType.INDIVIDUAL);

        boolean result = bookingService.cancelAppointment(appointment);
        if (result) {
            System.out.println("✅ Appointment cancelled");
        }
    }

    // =========================
    // ADMIN CANCEL
    // =========================
    static void adminCancel() {
        System.out.print("Date of appointment to cancel (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        System.out.print("Time (HH:MM): ");
        String time = scanner.nextLine();

        Administrator admin = new Administrator("admin", "1234");
        Appointment appointment = new Appointment(date, time, 1, 1,
                new User("user", "0000"), AppointmentType.INDIVIDUAL);

        bookingService.adminCancel(appointment, admin);
    }
}