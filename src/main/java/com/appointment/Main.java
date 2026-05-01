package com.appointment;

import com.appointment.domain.*;
import com.appointment.notifications.EmailService;
import com.appointment.notifications.Observer;
import com.appointment.service.*;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main entry point for the Appointment Scheduling System.
 * Provides a CLI menu for both administrators and regular users
 * to view, book, modify and cancel appointments.
 *
 * @author Appointment Team
 * @version 1.0
 */
public class Main {

    // ============================================================
    // ✅ Code Smell 1 — Logger replaces direct System.out usage
    //    SonarCloud rule: java:S106
    // ============================================================
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    // ============================================================
    // ✅ Code Smell 2 — Constant for repeated literal "Choose: "
    //    SonarCloud rule: java:S1192 (used 3+ times)
    // ============================================================
    private static final String CHOOSE = "Choose: ";

    // ============================================================
    // ✅ Code Smell 3 — Constant for repeated literal "❌ Invalid choice"
    //    SonarCloud rule: java:S1192 (used 4+ times)
    // ============================================================
    private static final String INVALID_CHOICE = "❌ Invalid choice";

    // Extra constants to remove other duplicated literals
    private static final String NO_APPOINTMENTS = "❌ No appointments found";
    private static final String NO_SLOTS = "❌ No available slots";
    private static final String DATE_PROMPT = "Enter date (YYYY-MM-DD): ";
    private static final String TIME_PROMPT = "Enter time (HH:MM): ";
    private static final String SLOTS_HEADER = "\n--- Available Slots ---";
    private static final String APPOINTMENTS_HEADER = "\n--- All Appointments ---";
    private static final String CANCELLED_STATUS = "Cancelled";

    static AuthService authService = new AuthService();
    static BookingService bookingService;
    static ScheduleService scheduleService = new ScheduleService();
    static Observer observer;
    static ReminderService reminderService;
    static Scanner scanner = new Scanner(System.in);
    static User currentUser = null;

    public static void main(String[] args) {

        // ✅ Load credentials from .env
        Dotenv dotenv = Dotenv.load();
        String email = dotenv.get("EMAIL_USERNAME");
        String password = dotenv.get("EMAIL_PASSWORD");

        // ✅ Use real EmailService
        observer = new EmailService(email, password);
        reminderService = new ReminderService(observer);
        bookingService = new BookingService(observer, scheduleService);

        System.out.println("========================================");
        System.out.println("   Appointment Scheduling System       ");
        System.out.println("========================================");

        boolean running = true;

        while (running) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Login as Administrator");
            System.out.println("2. Continue as User");
            System.out.println("3. Exit");
            System.out.print(CHOOSE);

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> adminMenu();
                case "2" -> userMenu();
                case "3" -> {
                    LOGGER.info("Goodbye!");
                    running = false;
                }
                default -> LOGGER.warning(INVALID_CHOICE);
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
            LOGGER.warning("❌ Invalid credentials");
            return;
        }

        LOGGER.info("✅ Admin logged in!");

        boolean running = true;
        while (running) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. View available slots");
            System.out.println("2. Add new slot");
            System.out.println("3. Cancel any appointment");
            System.out.println("4. View all appointments");
            System.out.println("5. Logout");
            System.out.print(CHOOSE);

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> viewSlots();
                case "2" -> addSlot();
                case "3" -> adminCancel();
                case "4" -> viewAllAppointments();
                case "5" -> {
                    authService.logout();
                    LOGGER.info("✅ Logged out");
                    running = false;
                }
                default -> LOGGER.warning(INVALID_CHOICE);
            }
        }
    }

    // =========================
    // ADD SLOT
    // =========================
    static void addSlot() {
        System.out.print("Enter slot time (HH:MM): ");
        String time = scanner.nextLine();
        scheduleService.addSlot(time);
    }

    // =========================
    // VIEW ALL APPOINTMENTS
    // =========================
    static void viewAllAppointments() {
        List<Appointment> appointments = bookingService.getAppointments();

        if (appointments.isEmpty()) {
            LOGGER.warning(NO_APPOINTMENTS);
            return;
        }

        System.out.println(APPOINTMENTS_HEADER);
        int i = 1;
        for (Appointment a : appointments) {
            System.out.println(i + ". " +
                    "Date: " + a.getDate() +
                    " | Time: " + a.getTime() +
                    " | User: " + a.getUser().getUsername() +
                    " | Type: " + a.getType() +
                    " | Status: " + a.getStatus()
            );
            i++;
        }
    }

    // =========================
    // USER MENU
    // =========================
    static void userMenu() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        currentUser = new User(name, "0000", email);

        boolean running = true;
        while (running) {
            System.out.println("\n--- User Menu ---");
            System.out.println("1. View available slots");
            System.out.println("2. Book appointment");
            System.out.println("3. Cancel appointment");
            System.out.println("4. Back");
            System.out.print(CHOOSE);

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> viewSlots();
                case "2" -> bookAppointment();
                case "3" -> cancelAppointment();
                case "4" -> running = false;
                default -> LOGGER.warning(INVALID_CHOICE);
            }
        }
    }

    // =========================
    // VIEW SLOTS
    // =========================
    static void viewSlots() {
        List<AppointmentSlot> slots = scheduleService.getAvailableSlots();
        if (slots.isEmpty()) {
            LOGGER.warning(NO_SLOTS);
            return;
        }
        System.out.println(SLOTS_HEADER);
        for (int i = 0; i < slots.size(); i++) {
            System.out.println((i + 1) + ". " + slots.get(i).getTime());
        }
    }

    // =========================
    // BOOK APPOINTMENT
    // =========================
    static void bookAppointment() {
        List<AppointmentSlot> slots = scheduleService.getAvailableSlots();

        if (slots.isEmpty()) {
            LOGGER.warning(NO_SLOTS);
            return;
        }

        System.out.println(SLOTS_HEADER);
        for (int i = 0; i < slots.size(); i++) {
            System.out.println((i + 1) + ". " + slots.get(i).getTime());
        }

        System.out.print("Choose slot number: ");
        int choice = Integer.parseInt(scanner.nextLine()) - 1;

        if (choice < 0 || choice >= slots.size()) {
            LOGGER.warning(INVALID_CHOICE);
            return;
        }

        AppointmentSlot selectedSlot = slots.get(choice);
        String time = selectedSlot.getTime();

        System.out.print("Date (YYYY-MM-DD): ");
        String date = scanner.nextLine();
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

        Appointment appointment = new Appointment(
                date, time, duration, participants, currentUser, type
        );

        AppointmentValidator validator = new AppointmentValidator();
        if (!validator.validate(appointment)) {
            LOGGER.warning("❌ Appointment not valid");
            return;
        }

        // ✅ Check if slot is already taken at the same date and time
        boolean slotTaken = false;
        for (Appointment a : bookingService.getAppointments()) {
            if (a.getDate().equals(date) && a.getTime().equals(time)
                    && !a.getStatus().equals(CANCELLED_STATUS)) {
                slotTaken = true;
                break;
            }
        }

        if (slotTaken) {
            LOGGER.warning("❌ This slot is already booked for this date and time");
            return;
        }

        bookingService.book(appointment);

        // ✅ Send real email reminder
        reminderService.sendReminder(appointment);

        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info(String.format("✅ Appointment booked: %s at %s", date, time));
        }
        LOGGER.info("📧 Email reminder sent!");
    }

    // =========================
    // CANCEL APPOINTMENT
    // =========================
    static void cancelAppointment() {
        List<Appointment> appointments = bookingService.getAppointments();

        if (appointments.isEmpty()) {
            LOGGER.warning(NO_APPOINTMENTS);
            return;
        }

        System.out.println("\n--- Your Appointments ---");
        int i = 1;
        for (Appointment a : appointments) {
            if (a.getUser().getUsername().equals(currentUser.getUsername())
                    && !a.getStatus().equals(CANCELLED_STATUS)) {
                System.out.println(i + ". Date: " + a.getDate() +
                        " | Time: " + a.getTime() +
                        " | Status: " + a.getStatus());
            }
            i++;
        }

        System.out.print(DATE_PROMPT);
        String date = scanner.nextLine();
        System.out.print(TIME_PROMPT);
        String time = scanner.nextLine();

        bookingService.cancelAppointment(date, time);
    }

    // =========================
    // ADMIN CANCEL
    // =========================
    static void adminCancel() {
        List<Appointment> appointments = bookingService.getAppointments();

        if (appointments.isEmpty()) {
            LOGGER.warning(NO_APPOINTMENTS);
            return;
        }

        System.out.println(APPOINTMENTS_HEADER);
        int i = 1;
        for (Appointment a : appointments) {
            if (!a.getStatus().equals(CANCELLED_STATUS)) {
                System.out.println(i + ". Date: " + a.getDate() +
                        " | Time: " + a.getTime() +
                        " | User: " + a.getUser().getUsername() +
                        " | Status: " + a.getStatus());
            }
            i++;
        }

        System.out.print(DATE_PROMPT);
        String date = scanner.nextLine();
        System.out.print(TIME_PROMPT);
        String time = scanner.nextLine();

        Administrator admin = new Administrator("admin", "1234");
        bookingService.adminCancel(date, time, admin);
    }
}
