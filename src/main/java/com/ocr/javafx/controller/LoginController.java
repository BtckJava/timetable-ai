package com.ocr.javafx.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController extends BaseController {

    @FXML
    private Label errorLabel;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    @FXML
    void goToRegister(ActionEvent event) {
        try {
            Stage stage = (Stage) txtEmail.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/ocr/javafx/register.fxml") // 🔥 FIX PATH
            );

            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            e.printStackTrace();
            showError("Cannot open Register screen");
        }
    }

    @FXML
    void handleForgotPassword(ActionEvent event) {
        //
    }

    @FXML
    void handleLogin(ActionEvent event) {
        String mail = txtEmail.getText();
        String password = txtPassword.getText();

        // Validate empty input
        if (mail.isEmpty()) {
            showError("Mail cannot be empty");
            return;
        }

        if (password.isEmpty()) {
            showError("Password cannot be empty");
            return;
        }

        // Validate exceed length
        if (mail.length() > 30) {
            showError("Email must not exceed 30 characters");
            return;
        }

        if (password.length() > 30) {
            showError("Password must not exceed 30 characters");
            return;
        }

        // Wrong mail format
        if (!mail.contains("@")) {
            showError("Invalid email format");
            return;
        }

        // Login logic (demo thui)
        if (mail.equals("nhi@") && password.equals("hanh")) {
            showSuccess("Login successful!");
            // chuyển sang trang chủ
            try {
                Stage stage = (Stage) txtEmail.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/home.fxml"));
                stage.setScene(new Scene(loader.load()));
            } catch (Exception e) {
                e.printStackTrace();
                showError("Cannot open Home screen");
            }
        }
        else {
            showError("Invalid username or password");
        }
    }

    @FXML
    public void initialize() {
        // gán errorLabel cho BaseController
        super.errorLabel = this.errorLabel;

        // clear lỗi khi user nhập lại
        txtEmail.textProperty().addListener((obs, oldVal, newVal) -> clearError());
        txtPassword.textProperty().addListener((obs, oldVal, newVal) -> clearError());
    }
}

