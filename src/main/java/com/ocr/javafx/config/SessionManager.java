package com.ocr.javafx.config;

import com.ocr.javafx.entity.User;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SessionManager {
    private User currentUser;

    public Long getCurrentUserId() {
        if (currentUser == null) return null;
        return currentUser.getId();
    }

    public void clear() {
        this.currentUser = null;
    }
}
