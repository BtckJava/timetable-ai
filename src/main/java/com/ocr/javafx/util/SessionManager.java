package com.ocr.javafx.util;

import com.ocr.javafx.entity.User;
import lombok.Getter;
import lombok.Setter;

public class SessionManager {
    @Getter
    @Setter
    private static User currentUser;

    public static void clearSession() {
        currentUser = null;
    }
}