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
 * Central service for managing appointment bookings, modifications, and cancellations
 * within the Appointment Scheduling System.
 *
 * <p>This service is the core orchestrator of the booking workflow. It coordinates between
 * three main collaborators:</p>
 * <ul>
 *   <li>{@link BookingRuleStrategy} — a list of validation rules applied before any booking
 *       is confirmed. Each rule encapsulates a distinct business constraint
 *       (duration limits, participant counts, appointment type restrictions, etc.).</li>
 *   <li>{@link Observer} — used to notify users when their appointments are cancelled,
 *       typically via email through the {@code EmailService} implementation.</li>
 *   <li>{@link ScheduleService} — manages available time slots and is responsible for
 *       freeing them after a cancellation.</li>
 * </ul>
 *
 * <h2>Design Patterns</h2>
 * <p>This class implements two well-known design patterns:</p>
 * <ul>
 *   <li><b>Strategy Pattern</b> — the {@link #rules} list holds interchangeable
 *       {@link BookingRuleStrategy} implementations. New rules can be plugged in via
 *       {@link #addRule(BookingRuleStrategy)} without modifying this class
 *       (Open/Closed Principle).</li>
 *   <li><b>Observer Pattern</b> — the injected {@link Observer} is notified of relevant
 *       events (e.g., cancellation), decoupling notification logic from booking logic.</li>
 * </ul>
 *
 * <h2>Persistence</h2>
 * <p>Appointments are stored both in memory (using a {@link LinkedList}) and on disk
 * (in a CSV-style flat file located at {@value #FILE_PATH}). The in-memory list is loaded
 * from the file at construction time and synchronized with disk on every booking and
 * cancellation. The chosen format per line is:</p>
 *
 * <pre>{@code date,time,duration,participants,username,type,status}</pre>
 *
 * <h2>Authorization</h2>
 * <p>Two levels of access control are enforced:</p>
 * <ul>
 *   <li><b>User-level</b> operations: {@link #book(Appointment)},
 *       {@link #cancelAppointment(String, String)}, {@link #modifyAppointment(Appointment, String, String)}.
 *       Restricted to future appointments only.</li>
 *   <li><b>Administrator-level</b> operations: {@link #adminCancel(String, String, Administrator)},
 *       {@link #adminModify(Appointment, Administrator, String, String)}.
 *       Bypass time restrictions but require a non-{@code null} {@link Administrator} reference.</li>
 * </ul>
 *
 * <h2>Typical usage</h2>
 * <pre>{@code
 * // 1. Create the service with its collaborators
 * Observer mailer = new EmailService("admin@gmail.com", "app-password");
 * ScheduleService schedule = new ScheduleService();
 * BookingService booking = new BookingService(mailer, schedule);
 *
 * // 2. Plug in the validation rules (Strategy Pattern)
 * booking.addRule(new DurationRule());
 * booking.addRule(new ParticipantLimitRule());
 *
 * // 3. Book an appointment
 * Appointment a = new Appointment("2026-06-01", "10:00", 1, 1, user, AppointmentType.URGENT);
 * boolean ok = booking.book(a);   // true if all rules pass
 *
 * // 4. Cancel an appointment (notification is sent automatically)
 * booking.cancelAppointment("2026-06-01", "10:00");
 * }</pre>
 *
 * <h2>Thread-safety</h2>
 * <p>This class is <b>not thread-safe</b>. The internal {@link LinkedList} is mutated by
 * multiple methods without synchronization. If concurrent access is required, callers must
 * provide external synchronization or wrap the service.</p>
 *
 * @author  Abdullah Najjar, Ezz Al-Din Zaben, Mohammed Al-Saadi
 * @version 1.0
 * @since   2026-04-30
 * @see     Appointment
 * @see     BookingRuleStrategy
 * @see     Observer
 * @see     ScheduleService
 * @see     Administrator
 */
public class BookingService {

    // ============================================================
    // Constants
    // ============================================================

    /**
     * Relative path of the flat file used for persistent storage of appointments.
     * <p>The file is created automatically on the first successful booking. Each line
     * encodes a single appointment in CSV form. The path is resolved relative to the
     * current working directory of the JVM.</p>
     */
    private static final String FILE_PATH = "appointments.txt";

    // ============================================================
    // Collaborators (injected via constructor)
    // ============================================================

    /**
     * Observer that receives notifications when appointments change state.
     * <p>Typically an instance of {@code EmailService} (which sends real e-mail through
     * Gmail SMTP) or {@code NotificationService} (a console-based mock used in tests).</p>
     *
     * @see Observer
     */
    private final Observer observer;

    /**
     * Schedule service used to query and update the catalogue of available slots.
     * <p>Currently held for future extensibility — when slot freeing on cancellation is
     * fully wired through {@code ScheduleService}, this reference will be used to mark
     * the released slot as available again.</p>
     *
     * @see ScheduleService
     */
    private final ScheduleService scheduleService;

    // ============================================================
    // State
    // ============================================================

    /**
     * Ordered list of validation rules consulted before confirming a booking.
     * <p>Rules are evaluated in insertion order. The first rule that returns
     * {@code false} short-circuits the validation and causes
     * {@link #book(Appointment)} to reject the appointment.</p>
     *
     * <p>This list realizes the <b>Strategy Pattern</b> — each rule is an independent
     * strategy that can be swapped, removed, or added without changing this class.</p>
     */
    private final List<BookingRuleStrategy> rules = new LinkedList<>();

    /**
     * In-memory cache of every appointment currently known to the service, including
     * cancelled ones.
     * <p>The list is populated from disk in {@link #loadFromFile()} during construction
     * and kept in sync with the file by {@link #saveToFile(Appointment)} (append) and
     * {@link #updateFile()} (rewrite).</p>
     *
     * <p>A {@link LinkedList} was chosen over {@link java.util.ArrayList} because the
     * dominant operations are sequential iteration and append.</p>
     */
    private final List<Appointment> appointments = new LinkedList<>();

    // ============================================================
    // Construction
    // ============================================================

    /**
     * Creates a new {@code BookingService} and immediately loads any previously persisted
     * appointments from disk.
     *
     * <p>The constructor performs side effects: it reads the file at {@value #FILE_PATH}
     * (if it exists) and rehydrates {@link #appointments} with the parsed entries. A
     * missing file is not an error — the service simply starts with an empty list.</p>
     *
     * <p>Validation rules are <b>not</b> registered automatically; clients must wire them
     * after construction using {@link #addRule(BookingRuleStrategy)}.</p>
     *
     * @param observer        a non-{@code null} notification observer that will be invoked
     *                        on cancellation; typically {@code EmailService} in production
     *                        and a mock observer in unit tests
     * @param scheduleService a non-{@code null} schedule service used to manage the catalogue
     *                        of bookable slots
     * @see #addRule(BookingRuleStrategy)
     * @see #loadFromFile()
     */
    public BookingService(Observer observer, ScheduleService scheduleService) {
        this.observer = observer;
        this.scheduleService = scheduleService;
        loadFromFile();
    }

    // ============================================================
    // Rule registration
    // ============================================================

    /**
     * Appends a validation rule to the end of the rule chain.
     *
     * <p>Each rule is a strategy implementing {@link BookingRuleStrategy#isValid(Appointment)}.
     * When {@link #book(Appointment)} is called, every registered rule is consulted in the
     * order they were added; the first rule to return {@code false} aborts the booking.</p>
     *
     * <p>This method enables runtime composition of validation policy — for example,
     * different deployments can register different rule sets without subclassing.</p>
     *
     * @param rule the rule strategy to add; must not be {@code null}
     * @see BookingRuleStrategy
     * @see #book(Appointment)
     */
    public void addRule(BookingRuleStrategy rule) {
        rules.add(rule);
    }

    // ============================================================
    // User-level operations
    // ============================================================

    /**
     * Attempts to book the given appointment.
     *
     * <p>The booking succeeds if and only if <i>every</i> registered
     * {@link BookingRuleStrategy} returns {@code true} for this appointment. On success,
     * the appointment's status is set to confirmed (via {@link Appointment#confirm()}),
     * the appointment is appended to the in-memory list, and a new line is appended
     * to the persistent file.</p>
     *
     * <p><b>Side effects on success:</b></p>
     * <ul>
     *   <li>{@link Appointment#confirm()} is invoked on the argument.</li>
     *   <li>The appointment is added to {@link #appointments}.</li>
     *   <li>The appointment is appended to {@value #FILE_PATH}.</li>
     * </ul>
     *
     * <p><b>Failure mode:</b> if any rule rejects the appointment, the method returns
     * immediately with {@code false} and no state is modified.</p>
     *
     * @param appointment the appointment to book; must not be {@code null} and must carry
     *                    a non-{@code null} {@link User} and {@link AppointmentType}
     * @return {@code true} if every rule accepted the appointment and it was persisted;
     *         {@code false} if any rule rejected it (the appointment is not booked)
     * @see BookingRuleStrategy#isValid(Appointment)
     * @see Appointment#confirm()
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
     * Updates the date and time of an existing appointment, provided the appointment
     * is still in the future.
     *
     * <p>Past appointments cannot be modified by regular users (use
     * {@link #adminModify(Appointment, Administrator, String, String)} to override this
     * restriction).</p>
     *
     * @param appointment the appointment to modify; must not be {@code null}
     * @param newDate     the new date, formatted as {@code YYYY-MM-DD}
     * @param newTime     the new time, formatted as {@code HH:MM} (24-hour)
     * @return {@code true} if the appointment was modified successfully;
     *         {@code false} if the appointment is in the past
     * @see Appointment#isFuture()
     * @see #adminModify(Appointment, Administrator, String, String)
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
     * Cancels the appointment that matches the given date and time, then notifies the
     * affected user.
     *
     * <p>This is the user-level cancellation method. It finds the first appointment in
     * {@link #appointments} whose date and time match the arguments. If the matched
     * appointment is in the past, the cancellation is rejected.</p>
     *
     * <p><b>Side effects on successful cancellation:</b></p>
     * <ol>
     *   <li>{@link Appointment#cancel()} is called, marking the status as cancelled.</li>
     *   <li>The participant count is reset to zero.</li>
     *   <li>The persistence file is rewritten without the cancelled entry
     *       ({@link #updateFile()}).</li>
     *   <li>The injected {@link Observer} is notified with a human-readable cancellation
     *       message — typically resulting in an email being sent to the user.</li>
     * </ol>
     *
     * @param date the date of the appointment to cancel ({@code YYYY-MM-DD})
     * @param time the time of the appointment to cancel ({@code HH:MM})
     * @return {@code true} if a matching future appointment was found and cancelled;
     *         {@code false} if no match exists or the appointment is in the past
     * @see Observer#notify(User, String)
     * @see #adminCancel(String, String, Administrator)
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

                // ✅ Send cancellation email through the Observer
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

    // ============================================================
    // Administrator-level operations
    // ============================================================

    /**
     * Cancels every appointment matching the given date and time, on behalf of an
     * administrator.
     *
     * <p>Unlike {@link #cancelAppointment(String, String)}, this method:</p>
     * <ul>
     *   <li>Requires a non-{@code null} {@link Administrator} — passing {@code null}
     *       results in a refusal and {@code false} return.</li>
     *   <li>Cancels <b>all</b> matching active appointments at the given time slot,
     *       not just the first one.</li>
     *   <li>Skips any appointment whose status is already {@code "Cancelled"}.</li>
     * </ul>
     *
     * @param date  the date of the appointments to cancel ({@code YYYY-MM-DD})
     * @param time  the time of the appointments to cancel ({@code HH:MM})
     * @param admin the administrator performing the cancellation; must not be {@code null}
     * @return {@code true} if at least one appointment was cancelled; {@code false} if
     *         {@code admin} is {@code null} or no active appointment matched
     * @see Administrator
     * @see #cancelAppointment(String, String)
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
     * Updates the date and time of an arbitrary appointment, on behalf of an administrator.
     *
     * <p>This privileged method bypasses the future-date check enforced by
     * {@link #modifyAppointment(Appointment, String, String)}. It is intended for cases
     * where an administrator needs to correct historical data or reschedule on behalf of
     * a user.</p>
     *
     * @param appointment the appointment to modify; must not be {@code null}
     * @param admin       the administrator performing the modification; must not be {@code null}
     * @param newDate     the new date ({@code YYYY-MM-DD})
     * @param newTime     the new time ({@code HH:MM})
     * @return {@code true} if the modification succeeded; {@code false} if {@code admin}
     *         is {@code null}
     * @see Administrator
     * @see #modifyAppointment(Appointment, String, String)
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

    // ============================================================
    // Persistence (private helpers)
    // ============================================================

    /**
     * Appends a single appointment as a new CSV line to the persistence file.
     *
     * <p>The file is opened in append mode, so existing entries are preserved. If the
     * file does not exist, it is created. I/O errors are caught and reported to
     * {@code System.out} but never propagated — booking flow is not interrupted by a
     * persistence failure (best-effort durability).</p>
     *
     * @param appointment the appointment to serialize; must not be {@code null}
     * @see #updateFile()
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
     * Reads the persistence file at startup and populates {@link #appointments}.
     *
     * <p>Each non-empty line is split on commas and the seven fields are mapped onto a
     * new {@link Appointment} instance. The associated {@link User} is reconstructed
     * with placeholder credentials ({@code "0000"} and {@code "unknown@email.com"}),
     * since user passwords and emails are not part of the appointment record.</p>
     *
     * <p>Lines that do not contain exactly seven comma-separated fields are silently
     * skipped (defensive parsing). I/O errors are caught and reported but never
     * propagated.</p>
     *
     * @see #saveToFile(Appointment)
     */
    private void loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 7) {
                    User user = new User(parts[4], "0000", "unknown@email.com");
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
     * Rewrites the persistence file from scratch, omitting any cancelled appointments.
     *
     * <p>This method is invoked after a cancellation to prune the file. It opens the
     * file in <b>truncate</b> mode (not append) and writes back every appointment in
     * {@link #appointments} whose status is not {@code "Cancelled"}.</p>
     *
     * <p><b>Trade-off:</b> using rewrite-on-cancel keeps the file size bounded but is
     * {@code O(n)} in the number of live appointments. For the in-class scope of this
     * project, this is acceptable.</p>
     *
     * @see #saveToFile(Appointment)
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

    // ============================================================
    // Accessors
    // ============================================================

    /**
     * Returns the live, in-memory list of all appointments known to this service —
     * including cancelled ones.
     *
     * <p><b>Warning:</b> this method returns the internal {@link LinkedList} directly,
     * not a defensive copy. Callers must not mutate the returned list, otherwise the
     * service's invariants (and the persistence file) will be corrupted. The list is
     * exposed in this form for compatibility with the existing test suite.</p>
     *
     * @return the underlying list of appointments; never {@code null}, possibly empty
     */
    public List<Appointment> getAppointments() {
        return appointments;
    }
}