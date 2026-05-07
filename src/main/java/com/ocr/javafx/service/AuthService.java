package com.ocr.javafx.service;

import com.ocr.javafx.dto.request.ChangePasswordRequest;
import com.ocr.javafx.dto.request.LoginRequest;
import com.ocr.javafx.dto.request.RegisterRequest;
import com.ocr.javafx.dto.response.AuthResponse;
import com.ocr.javafx.entity.User;
import com.ocr.javafx.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

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

    public String generateAndSaveOtp(String email) throws Exception {
        if (email == null || email.isBlank()) {
            throw new Exception("Vui lòng nhập email.");
        }
        User user = repository.findByEmail(email.trim());
        if (user == null) {
            throw new Exception("Email không tồn tại trong hệ thống");
        }

        String otp = String.format("%06d", ThreadLocalRandom.current().nextInt(0, 1_000_000));
        user.setOtpCode(otp);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5));
        repository.save(user);

        try {
            // Tạo chuỗi JSON chứa email và otp
            String jsonBody = String.format("{\"email\": \"%s\", \"otp\": \"%s\"}", email.trim(), otp);

            // Build Request
            HttpRequest request = HttpRequest.newBuilder()
                    // Thay cổng 8080 nếu Mail Server của bạn chạy cổng khác
                    .uri(URI.create("http://localhost:8080/api/mail/send-otp"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            // Gửi Request
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Kiểm tra kết quả trả về từ Mail Server
            if (response.statusCode() != 200) {
                // Nếu gửi mail lỗi, có thể bạn sẽ muốn xóa OTP đi hoặc ném lỗi để UI hiện thông báo
                throw new Exception("Lỗi từ Mail Server: " + response.body());
            }

            System.out.println("Đã gọi API gửi OTP thành công tới: " + email);

        } catch (java.net.ConnectException e) {
            throw new Exception("Không thể kết nối đến Mail Server. Vui lòng kiểm tra xem Server đã bật chưa.");
        } catch (Exception e) {
            throw new Exception("Lỗi khi gửi email: " + e.getMessage());
        }

        System.out.println("Mock Gửi Mail - OTP là: " + otp);
        return otp;
    }

    public boolean verifyOtp(String email, String inputOtp) throws Exception {
        if (email == null || email.isBlank()) {
            throw new Exception("Vui lòng nhập email.");
        }
        User user = repository.findByEmail(email.trim());
        if (user == null) {
            throw new Exception("Email không tồn tại trong hệ thống");
        }
        if (inputOtp == null || inputOtp.isBlank()
                || user.getOtpCode() == null
                || !user.getOtpCode().equals(inputOtp.trim())) {
            throw new Exception("Mã OTP không hợp lệ");
        }
        if (user.getOtpExpiryTime() == null || LocalDateTime.now().isAfter(user.getOtpExpiryTime())) {
            throw new Exception("Mã OTP đã hết hạn");
        }
        return true;
    }

    public void resetPassword(String email, String newPassword) throws Exception {
        if (email == null || email.isBlank()) {
            throw new Exception("Vui lòng nhập email.");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new Exception("Vui lòng nhập mật khẩu mới.");
        }
        User user = repository.findByEmail(email.trim());
        if (user == null) {
            throw new Exception("Email không tồn tại trong hệ thống");
        }

        String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        user.setPassword(hashed);
        user.setOtpCode(null);
        user.setOtpExpiryTime(null);
        repository.save(user);
    }

}