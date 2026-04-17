package com.ocr.javafx.dto.response;

import com.ocr.javafx.entity.User;
import lombok.Getter;

@Getter
public class AuthResponse {
    private final boolean success;
    private final String response;
    private User user;

    public AuthResponse(boolean success, String response, User user){
        this.success = success;
        this.response = response;
        this.user = user;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getResponse() {
        return response;
    }
}
