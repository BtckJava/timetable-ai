package com.ocr.javafx.controller.login;

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

    @FXML
    void goToLogin(ActionEvent event) {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ocr/javafx/login/login.fxml"));
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            e.printStackTrace();
            showError("Cannot open Login screen");
        }
    }

    @FXML
    void handleRegister(ActionEvent event) {
        RegisterRequest request = new RegisterRequest(
                usernameField.getText(),
                emailField.getText(),
                passwordField.getText(),
                confirmPasswordField.getText()
        );
        clearError();

        AuthService service = new AuthService();
        AuthResponse response = service.register(request);

        if(response.isSuccess()){
            goToLogin(null);
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
