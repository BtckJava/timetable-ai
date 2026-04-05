package com.ocr.javafx.service;

import com.ocr.javafx.dto.request.LoginRequest;
import com.ocr.javafx.dto.request.RegisterRequest;
import com.ocr.javafx.dto.response.AuthResponse;
import com.ocr.javafx.entity.User;
import com.ocr.javafx.repository.UserRepository;
import com.ocr.javafx.util.SessionManager;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {
    private final UserRepository repository;

    public AuthService(UserRepository repository) {
        this.repository = repository;
    }

    public AuthResponse register(RegisterRequest request){
        if(request.getUsername() == null || request.getUsername().isEmpty()){
            return new AuthResponse(false, "Please enter a username.");
        }

        if(request.getEmail() == null || request.getEmail().isEmpty()){
            return new AuthResponse(false, "Please enter your email address.");
        }

        if(request.getPassword() == null || request.getPassword().isEmpty()){
            return new AuthResponse(false, "Please enter a password.");
        }

        if(request.getConfirmPassword() == null || request.getConfirmPassword().isEmpty()){
            return new AuthResponse(false, "Please confirm your password.");
        }

        if(!request.getPassword().equals(request.getConfirmPassword())) {
            return new AuthResponse(false, "Passwords do not match. Please try again.");
        }

        if(!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return new AuthResponse(false, "Please enter a valid email address (e.g., user@example.com).");
        }

        if(repository.findByEmail(request.getEmail()) != null){
            return new AuthResponse(false, "An account with this email already exists. Try logging in.");
        }

        String hashed = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(hashed);

        repository.save(user);

        return new AuthResponse(true, "Register successful");
    }

    public AuthResponse login(LoginRequest request){
        User user = repository.findByEmail(request.getEmail());
        if(user == null){
            return new AuthResponse(false, "User not found");
        }

        if(!BCrypt.checkpw(request.getPassword(), user.getPassword())){
            return new AuthResponse(false, "Incorrect email or password");
        }
        SessionManager.setCurrentUser(user);
        return new AuthResponse(true, "Login successful");
    }
}
