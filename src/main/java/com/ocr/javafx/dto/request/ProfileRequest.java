package com.ocr.javafx.dto.request;

public class ProfileRequest {

    private String email;
    private String username;
    private String avatarPath;

    public ProfileRequest() {
    }

    public ProfileRequest(String email, String username, String avatarPath) {
        this.email = email;
        this.username = username;
        this.avatarPath = avatarPath;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }
}