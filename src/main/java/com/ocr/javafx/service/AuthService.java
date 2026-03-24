package com.ocr.javafx.service;

import com.ocr.javafx.dto.response.AuthResponse;
import com.ocr.javafx.entity.User;
import com.ocr.javafx.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {
    private UserRepository repository = new UserRepository();

    public AuthResponse register(String username, String email, String password){
        if(email == null || password == null || email.isEmpty() || password.isEmpty()){
            return new AuthResponse(false, "Fields cannot be empty");
        }

        if(!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return new AuthResponse(false, "Invalid email");
        }

        if(repository.findByEmail(email) != null){
            return new AuthResponse(false, "User already exists");
        }

        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(hashed);

        repository.save(user);

        return new AuthResponse(true, "Register successful");
    }

    public AuthResponse login(String email, String password){
        User user = repository.findByEmail(email);
        if(user == null){
            return new AuthResponse(false, "User not found");
        }

        if(!BCrypt.checkpw(password, user.getPassword())){
            return new AuthResponse(false, "Incorrect email or password");
        }

        return new AuthResponse(true, "Login successful");
    }
}
