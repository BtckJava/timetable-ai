package com.ocr.javafx.service;

import com.ocr.javafx.dto.request.LoginRequest;
import com.ocr.javafx.dto.request.RegisterRequest;
import com.ocr.javafx.dto.response.AuthResponse;
import com.ocr.javafx.entity.User;
import com.ocr.javafx.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {
    private UserRepository repository = new UserRepository();

    public AuthResponse register(RegisterRequest request){
        if(request.getUsername() == null || request.getUsername().isEmpty() ||
                request.getEmail() == null || request.getEmail().isEmpty() ||
                request.getPassword() == null || request.getEmail().isEmpty() ||
                request.getConfirmPassword() == null || request.getConfirmPassword().isEmpty()
        ){
            return new AuthResponse(false, "Please fill in all fields");
        }

        if(!request.getPassword().equals(request.getConfirmPassword())) {
            return new AuthResponse(false, "Password do not match");
        }

        if(!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return new AuthResponse(false, "Invalid email");
        }

        if(repository.findByEmail(request.getEmail()) != null){
            return new AuthResponse(false, "User already exists");
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

        return new AuthResponse(true, "Login successful");
    }
}
