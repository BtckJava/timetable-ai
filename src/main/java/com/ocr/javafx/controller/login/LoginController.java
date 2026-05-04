package com.ocr.javafx.controller.login;


import com.ocr.javafx.ApplicationContext;
import com.ocr.javafx.controller.base.BaseController;
import com.ocr.javafx.controller.main.MainController;
import com.ocr.javafx.dto.request.LoginRequest;
import com.ocr.javafx.dto.response.AuthResponse;
import com.ocr.javafx.service.AuthService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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


    @FXML
    private TextField passwordTextField;


    @Setter
    private ApplicationContext applicationContext;


    @FXML
    private void goToRegister(ActionEvent event) {
        switchToRegister();
    }
    @FXML
    private Button loginButton;


    private boolean isPasswordVisible = false;


    @FXML
    void togglePasswordVisible(ActionEvent event) {
        isPasswordVisible = !isPasswordVisible;
        passwordField.setVisible(!isPasswordVisible);
        passwordField.setManaged(!isPasswordVisible);
        passwordTextField.setVisible(isPasswordVisible);
        passwordTextField.setManaged(isPasswordVisible);
    }


    private void switchToRegister(){
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();


            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/ocr/javafx/login/register.fxml")
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


        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();


        if (email.isEmpty() || password.isEmpty()){
            showError("Please fill in all fields");
            return;
        }


        setLoadingState(true);


        Task<AuthResponse> loginTask = new Task<>() {
            @Override
            protected AuthResponse call() {
                LoginRequest request = new LoginRequest(email, password);
                return applicationContext.getAuthService().login(request);
            }
        };


        loginTask.setOnSucceeded(e -> {
            AuthResponse response = loginTask.getValue();
            setLoadingState(false);


            if (response.isSuccess()) {
                showSuccess("Login successful!");
                applicationContext.getSessionManager().setCurrentUser(response.getUser());
                navigateToMain();
            } else {
                showError(response.getResponse());
            }
        });


        loginTask.setOnFailed(e -> {
            setLoadingState(false);
            showError("An unexpected error occurred.");
        });


        new Thread(loginTask).start();
    }


    private void setLoadingState(boolean isLoading) {
        loginButton.setDisable(isLoading);
        loginButton.setText(isLoading ? "Logging in..." : "Login");
    }


    private void navigateToMain() {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setMinWidth(1000);
            stage.setMinHeight(750);
            stage.centerOnScreen();


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ocr/javafx/main/main.fxml"));
            stage.setScene(new Scene(loader.load()));


            MainController controller = loader.getController();
            controller.init(applicationContext);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Cannot open Home screen");
        }
    }


    @FXML
    private void handleMinimize(ActionEvent event) {
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.setIconified(true);
    }


    @FXML
    private void handleMaximize(ActionEvent event) {
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }


    @FXML
    private void handleExit(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }


    @FXML
    public void initialize() {
        // gán errorLabel cho BaseController
        super.errorLabel = this.errorLabel;
        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());
        // clear lỗi khi user nhập lại
        emailField.textProperty().addListener((obs, oldVal, newVal) -> clearError());
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> clearError());
    }
}
