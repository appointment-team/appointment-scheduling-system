package com.appointment.service;

import com.appointment.domain.Administrator;
import io.github.cdimascio.dotenv.Dotenv;


public class AuthService {

    // ✅ Load credentials from .env (not hard-coded anymore)
    private static final Dotenv DOTENV = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    private static final String ADMIN_USERNAME = getEnvOrDefault("ADMIN_USERNAME", "admin");
    private static final String ADMIN_PASSWORD = getEnvOrDefault("ADMIN_PASSWORD", "1234");

    private Administrator loggedInAdmin;


    private static String getEnvOrDefault(String key, String fallback) {
        String value = DOTENV.get(key);
        return (value == null || value.isBlank()) ? fallback : value;
    }

    public boolean login(String username, String password) {
        if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
            loggedInAdmin = new Administrator(username, password);
            return true;
        }
        return false;
    }

    public void logout() {
        loggedInAdmin = null;
    }

    public boolean isLoggedIn() {
        return loggedInAdmin != null;
    }
}