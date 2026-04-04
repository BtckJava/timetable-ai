package com.ocr.javafx.dto.response;

public class AuthResponse {
    private final boolean success;
    private final String response;

    public AuthResponse(boolean success, String response){
        this.success = success;
        this.response = response;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getResponse() {
        return response;
    }
}
