package com.ocr.javafx.service;

import com.ocr.javafx.entity.User;
import com.ocr.javafx.repository.UserRepository;

public class UserServiceImpl implements IUserService {
    // khởi tạo repository để làm việc với db
    private final UserRepository userRepository = new UserRepository();

    @Override
    public User getProfileByEmail(String email) {
        // gọi method findByEmail từ UserRepository
        // xử lý logic trước khi trả về dữ liệu
        return userRepository.findByEmail(email);
    }
}