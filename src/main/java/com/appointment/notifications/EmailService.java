package com.appointment.notifications;

import com.appointment.domain.User;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

/**
 * Sends real email notifications using Gmail SMTP.
 * @author YourName
 * @version 1.0
 */
public class EmailService implements Observer {

    private final String username;
    private final String password;

    /**
     * Constructs EmailService with Gmail credentials.
     * @param username Gmail address
     * @param password Gmail App Password
     */
    public EmailService(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Sends email notification to user.
     * @param user the user to notify
     * @param message the notification message
     */
    @Override
    public void notify(User user, String message) {
        sendEmail(user.getEmail(), // ← إيميل الـ user مش إيميلك
                "Appointment Reminder",
                "Dear " + user.getUsername() + ",\n\n" + message + "\n\nBest regards");
    }

    /**
     * Sends an email using Gmail SMTP.
     * @param to recipient email
     * @param subject email subject
     * @param body email body
     */
    public void sendEmail(String to, String subject, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(username));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject(subject);
            msg.setText(body);
            Transport.send(msg);
            System.out.println("✅ Email sent to: " + to);
        } catch (MessagingException e) {
            System.out.println("❌ Failed to send email: " + e.getMessage());
        }
    }
}