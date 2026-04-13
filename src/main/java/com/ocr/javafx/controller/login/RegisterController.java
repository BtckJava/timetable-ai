package com.ocr.javafx.controller.login;

import com.ocr.javafx.ApplicationContext;
import com.ocr.javafx.controller.base.BaseController;
import com.ocr.javafx.dto.request.RegisterRequest;
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
import lombok.Getter;
import lombok.Setter;

public class RegisterController extends BaseController {

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField emailField;

    @FXML
    private Label errorLabel;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;

    @Setter
    private ApplicationContext applicationContext;

    @FXML
    private void goToLogin(ActionEvent event) {
        switchToLogin();
    }

    private void switchToLogin(){
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ocr/javafx/login/login.fxml"));
            Scene scene = new Scene(loader.load());

            LoginController controller = loader.getController();
            controller.setApplicationContext(applicationContext);

            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Cannot open Login screen");
        }
    }

    @FXML
    void handleRegister(ActionEvent event) {
        clearError();

        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || email.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty()) {

            showError("Please fill in all fields");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        RegisterRequest request = new RegisterRequest(
                username, email, password, confirmPassword
        );

        AuthResponse response = applicationContext.getAuthService().register(request);

        if(response.isSuccess()){
            switchToLogin();
        } else{
            showError(response.getResponse());
        }
    }

    @FXML
    public void initialize() {
        // gán errorLabel cho BaseController
        super.errorLabel = this.errorLabel;

        // clear lỗi khi user nhập lại
        usernameField.textProperty().addListener((obs, o, n) -> clearError());
        emailField.textProperty().addListener((obs, o, n) -> clearError());
        passwordField.textProperty().addListener((obs, o, n) -> clearError());
        confirmPasswordField.textProperty().addListener((obs, o, n) -> clearError());
    }

}
