package com.ocr.javafx.service;

import com.ocr.javafx.entity.User;

public interface IUserService {
    // lấy thông tin người dùng bằng email
    User getProfileByEmail(String email);
}