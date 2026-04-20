package com.ocr.javafx.service;

import com.ocr.javafx.entity.User;

public class UserSession {
    private static User loggedInUser;

    public static void setInstance(User user) {
        loggedInUser = user;
    }

    public static User getInstance() {
        return loggedInUser;
    }

    public static String getEmail() {
        return loggedInUser != null ? loggedInUser.getEmail() : null;
    }

    public static Long getUserId() {
        return loggedInUser != null ? loggedInUser.getId() : null;
    }

    public static boolean isLoggedIn() {
        return loggedInUser != null;
    }

    public static void clear() {
        loggedInUser = null;
    }
}