package com.ocr.javafx.controller.login;

import com.ocr.javafx.ApplicationContext;
import com.ocr.javafx.controller.base.BaseController;
import com.ocr.javafx.controller.main.MainController;
import com.ocr.javafx.dto.request.LoginRequest;
import com.ocr.javafx.dto.response.AuthResponse;
import com.ocr.javafx.service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;

public class LoginController extends BaseController {

    @FXML
    private Label errorLabel;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @Setter
    private ApplicationContext applicationContext;

    @FXML
    private void goToRegister(ActionEvent event) {
        switchToRegister();
    }

    private void switchToRegister(){
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/ocr/javafx/login/register.fxml") // 🔥 FIX PATH
            );

            Scene scene = new Scene(loader.load());

            RegisterController controller = loader.getController();
            controller.setApplicationContext(applicationContext);

            stage.setScene(scene);
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
        clearError();

        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()){
            showError("Please fill in all fields");
            return;
        }

        LoginRequest request = new LoginRequest(
            email, password
        );

        AuthResponse response = applicationContext.getAuthService().login(request);

        // Login logic
        if (response.isSuccess()) {
            showSuccess("Login successful!");
            applicationContext.getSessionManager().setCurrentUser(response.getUser());
            // chuyển sang trang chủ
            try {
                Stage stage = (Stage) emailField.getScene().getWindow();

                stage.setMinWidth(1000);
                stage.setMinHeight(750);
                stage.setWidth(1000);
                stage.setHeight(750);
                stage.centerOnScreen();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ocr/javafx/main/main.fxml"));
                stage.setScene(new Scene(loader.load()));

                MainController controller = loader.getController();
                controller.init(applicationContext);


            } catch (Exception e) {
                e.printStackTrace();
                showError(e.getMessage());
                showError("Cannot open Home screen");
            }
        }
        else {
            showError(response.getResponse());
        }
    }

    @FXML
    public void initialize() {
        // gán errorLabel cho BaseController
        super.errorLabel = this.errorLabel;

        // clear lỗi khi user nhập lại
        emailField.textProperty().addListener((obs, oldVal, newVal) -> clearError());
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> clearError());
    }
}

