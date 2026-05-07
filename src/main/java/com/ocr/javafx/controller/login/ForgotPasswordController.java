package com.ocr.javafx.controller.login;

import com.ocr.javafx.ApplicationContext;
import com.ocr.javafx.util.NotificationUtils;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Setter;

public class ForgotPasswordController {

    @FXML
    private VBox emailStep;

    @FXML
    private VBox otpStep;

    @FXML
    private VBox passwordStep;

    @FXML
    private TextField emailField;

    @FXML
    private TextField otpField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label statusLabel;

    @FXML
    private Button sendOtpButton;

    @FXML
    private Button verifyOtpButton;

    @FXML
    private Button resetPasswordButton;

    @Setter
    private ApplicationContext applicationContext;

    private String currentEmail;

    @FXML
    public void initialize() {
        showStep(emailStep);
        emailField.textProperty().addListener((obs, oldVal, newVal) -> clearStatus());
        otpField.textProperty().addListener((obs, oldVal, newVal) -> clearStatus());
        newPasswordField.textProperty().addListener((obs, oldVal, newVal) -> clearStatus());
        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> clearStatus());
    }

    @FXML
    private void handleSendOtp(ActionEvent event) {
        if (applicationContext == null) {
            showError("Ứng dụng chưa sẵn sàng. Vui lòng thử lại.");
            return;
        }
        String email = emailField.getText() != null ? emailField.getText().trim() : "";
        if (email.isEmpty()) {
            showError("Vui lòng nhập email.");
            return;
        }
        setBusy(sendOtpButton, true, "Đang gửi...");

        Task<String> task = new Task<String>() {
            @Override
            protected String call() throws Exception {
                return applicationContext.getAuthService().generateAndSaveOtp(email);
            }
        };
        task.setOnSucceeded(e -> {
            setBusy(sendOtpButton, false, "Nhận mã OTP");
            currentEmail = email;
            NotificationUtils.showSuccess("Đã tạo OTP. Kiểm tra Console để test mã.");
            showInfo("OTP có hiệu lực trong 5 phút.");
            showStep(otpStep);
        });
        task.setOnFailed(e -> {
            setBusy(sendOtpButton, false, "Nhận mã OTP");
            showError(extractError(task.getException()));
        });
        runTask(task, "forgot-password-generate-otp");
    }

    @FXML
    private void handleVerifyOtp(ActionEvent event) {
        String otp = otpField.getText() != null ? otpField.getText().trim() : "";
        if (currentEmail == null || currentEmail.isBlank()) {
            showError("Vui lòng nhập email trước.");
            showStep(emailStep);
            return;
        }
        if (!otp.matches("\\d{6}")) {
            showError("OTP phải gồm đúng 6 chữ số.");
            return;
        }
        setBusy(verifyOtpButton, true, "Đang xác nhận...");

        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return applicationContext.getAuthService().verifyOtp(currentEmail, otp);
            }
        };
        task.setOnSucceeded(e -> {
            setBusy(verifyOtpButton, false, "Xác nhận");
            NotificationUtils.showSuccess("OTP hợp lệ. Vui lòng đặt mật khẩu mới.");
            showInfo("Nhập mật khẩu mới để hoàn tất.");
            showStep(passwordStep);
        });
        task.setOnFailed(e -> {
            setBusy(verifyOtpButton, false, "Xác nhận");
            showError(extractError(task.getException()));
        });
        runTask(task, "forgot-password-verify-otp");
    }

    @FXML
    private void handleResetPassword(ActionEvent event) {
        String newPassword = newPasswordField.getText() != null ? newPasswordField.getText() : "";
        String confirmPassword = confirmPasswordField.getText() != null ? confirmPasswordField.getText() : "";
        if (newPassword.isBlank() || confirmPassword.isBlank()) {
            showError("Vui lòng nhập đầy đủ mật khẩu mới.");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            showError("Xác nhận mật khẩu không khớp.");
            return;
        }
        if (newPassword.length() < 6) {
            showError("Mật khẩu mới nên có ít nhất 6 ký tự.");
            return;
        }
        setBusy(resetPasswordButton, true, "Đang lưu...");

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                applicationContext.getAuthService().resetPassword(currentEmail, newPassword);
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            setBusy(resetPasswordButton, false, "Lưu mật khẩu");
            NotificationUtils.showSuccess("Đổi mật khẩu thành công. Vui lòng đăng nhập lại.");
            switchToLogin();
        });
        task.setOnFailed(e -> {
            setBusy(resetPasswordButton, false, "Lưu mật khẩu");
            showError(extractError(task.getException()));
        });
        runTask(task, "forgot-password-reset-password");
    }

    @FXML
    private void handleBackToEmail(ActionEvent event) {
        showStep(emailStep);
    }

    @FXML
    private void handleBackToLogin(ActionEvent event) {
        switchToLogin();
    }

    @FXML
    private void handleMinimize(ActionEvent event) {
        ((Stage) emailField.getScene().getWindow()).setIconified(true);
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

    private void showStep(VBox activeStep) {
        emailStep.setVisible(activeStep == emailStep);
        emailStep.setManaged(activeStep == emailStep);
        otpStep.setVisible(activeStep == otpStep);
        otpStep.setManaged(activeStep == otpStep);
        passwordStep.setVisible(activeStep == passwordStep);
        passwordStep.setManaged(activeStep == passwordStep);
    }

    private void switchToLogin() {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ocr/javafx/login/login.fxml"));
            Scene scene = new Scene(loader.load());
            LoginController controller = loader.getController();
            controller.setApplicationContext(applicationContext);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Không thể quay về màn Login.");
        }
    }

    private void runTask(Task<?> task, String threadName) {
        Thread thread = new Thread(task, threadName);
        thread.setDaemon(true);
        thread.start();
    }

    private void setBusy(Button button, boolean busy, String idleText) {
        button.setDisable(busy);
        button.setText(idleText);
    }

    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #dc2626;");
        NotificationUtils.showError(message);
    }

    private void showInfo(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #0f766e;");
    }

    private void clearStatus() {
        statusLabel.setText("");
    }

    private String extractError(Throwable throwable) {
        if (throwable == null) {
            return "Có lỗi xảy ra. Vui lòng thử lại.";
        }
        Throwable root = throwable.getCause() != null ? throwable.getCause() : throwable;
        return root.getMessage() != null ? root.getMessage() : "Có lỗi xảy ra. Vui lòng thử lại.";
    }
}
