package com.ocr.javafx.service;

import com.ocr.javafx.dto.request.ChangePasswordRequest;
import com.ocr.javafx.dto.request.LoginRequest;
import com.ocr.javafx.dto.request.RegisterRequest;
import com.ocr.javafx.dto.response.AuthResponse;
import com.ocr.javafx.entity.User;
import com.ocr.javafx.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {
    private final UserRepository repository;

    public AuthService(UserRepository repository) {
        this.repository = repository;
    }

    public AuthResponse register(RegisterRequest request){
        if(request.getUsername() == null || request.getUsername().isEmpty()){
            return new AuthResponse(false, "Please enter a username.", null);
        }

        if(request.getEmail() == null || request.getEmail().isEmpty()){
            return new AuthResponse(false, "Please enter your email address.", null);
        }

        if(request.getPassword() == null || request.getPassword().isEmpty()){
            return new AuthResponse(false, "Please enter a password.", null);
        }

        if(request.getConfirmPassword() == null || request.getConfirmPassword().isEmpty()){
            return new AuthResponse(false, "Please confirm your password.", null);
        }

        if(!request.getPassword().equals(request.getConfirmPassword())) {
            return new AuthResponse(false, "Passwords do not match. Please try again.", null);
        }

        if(!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return new AuthResponse(false, "Please enter a valid email address (e.g., user@example.com).", null);
        }

        if(repository.findByEmail(request.getEmail()) != null){
            return new AuthResponse(false, "An account with this email already exists. Try logging in.", null);
        }

        String hashed = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(hashed);

        repository.save(user);


        return new AuthResponse(true, "Register successful", null);
    }

    public AuthResponse login(LoginRequest request){
        User user = repository.findByEmail(request.getEmail());
        if(user == null){
            return new AuthResponse(false, "User not found", null);
        }

        if(!BCrypt.checkpw(request.getPassword(), user.getPassword())){
            return new AuthResponse(false, "Incorrect email or password", null);
        }

        return new AuthResponse(true, "Login successful", user);
    }

    public AuthResponse changePassword(Long userId, ChangePasswordRequest request) {
        if (request.getOldPassword().isEmpty() || request.getNewPassword().isEmpty()) {
            return new AuthResponse(false, "Vui lòng nhập đầy đủ thông tin.", null);
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return new AuthResponse(false, "Xác nhận mật khẩu mới không khớp.", null);
        }

        User user = repository.findById(userId);
        if (user == null) {
            return new AuthResponse(false, "Không tìm thấy người dùng.", null);
        }

        if (!BCrypt.checkpw(request.getOldPassword(), user.getPassword())) {
            return new AuthResponse(false, "Mật khẩu cũ không chính xác.", null);
        }

        String hashed = BCrypt.hashpw(request.getNewPassword(), BCrypt.gensalt());
        user.setPassword(hashed);
        repository.save(user);

        return new AuthResponse(true, "Đổi mật khẩu thành công!", null);
    }

}