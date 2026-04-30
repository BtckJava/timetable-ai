package com.ocr.javafx.controller.login;


import com.ocr.javafx.ApplicationContext;
import com.ocr.javafx.controller.base.BaseController;
import com.ocr.javafx.dto.request.RegisterRequest;
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


    @FXML
    private TextField passwordTextField;


    @FXML
    private TextField confirmPasswordTextField;


    @FXML
    private Button registerButton;


    @Setter
    private ApplicationContext applicationContext;


    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;


    @FXML
    void togglePasswordVisible(ActionEvent event) {
        isPasswordVisible = !isPasswordVisible;
        passwordField.setVisible(!isPasswordVisible);
        passwordField.setManaged(!isPasswordVisible);
        passwordTextField.setVisible(isPasswordVisible);
        passwordTextField.setManaged(isPasswordVisible);
    }


    @FXML
    void toggleConfirmPasswordVisible(ActionEvent event) {
        isConfirmPasswordVisible = !isConfirmPasswordVisible;
        confirmPasswordField.setVisible(!isConfirmPasswordVisible);
        confirmPasswordField.setManaged(!isConfirmPasswordVisible);
        confirmPasswordTextField.setVisible(isConfirmPasswordVisible);
        confirmPasswordTextField.setManaged(isConfirmPasswordVisible);
    }


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


        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();


        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }


        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }


        registerButton.setDisable(true);
        registerButton.setText("Creating account...");


        Task<AuthResponse> registerTask = new Task<>() {
            @Override
            protected AuthResponse call() {
                RegisterRequest request = new RegisterRequest(username, email, password, confirmPassword);
                return applicationContext.getAuthService().register(request);
            }
        };


        registerTask.setOnSucceeded(e -> {
            AuthResponse response = registerTask.getValue();
            if(response.isSuccess()){
                switchToLogin();
            } else{
                showError(response.getResponse());
                resetButton();
            }
        });


        registerTask.setOnFailed(e -> {
            showError("Registration failed. Please try again.");
            resetButton();
        });


        new Thread(registerTask).start();
    }


    private void resetButton() {
        registerButton.setDisable(false);
        registerButton.setText("Register");
    }


    @FXML
    private void handleMinimize(ActionEvent event) {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setIconified(true);
    }


    @FXML
    private void handleMaximize(ActionEvent event) {
        Stage stage = (Stage) usernameField.getScene().getWindow();
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
        confirmPasswordTextField.textProperty().bindBidirectional(confirmPasswordField.textProperty());
        usernameField.textProperty().addListener((obs, o, n) -> clearError());
        emailField.textProperty().addListener((obs, o, n) -> clearError());
        passwordField.textProperty().addListener((obs, o, n) -> clearError());
        confirmPasswordField.textProperty().addListener((obs, o, n) -> clearError());
    }


}
