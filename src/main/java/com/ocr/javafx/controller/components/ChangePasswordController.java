package com.ocr.javafx.controller.components;

import com.ocr.javafx.ApplicationContext;
import com.ocr.javafx.controller.base.BaseController;
import com.ocr.javafx.dto.request.ChangePasswordRequest;
import com.ocr.javafx.dto.response.AuthResponse;
import com.ocr.javafx.util.Function.AlertUtils;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import lombok.Setter;

public class ChangePasswordController extends BaseController {

    @FXML
    private PasswordField oldPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button btnSave;

    private ApplicationContext applicationContext;
    private Long currentUserId;

    public void init(ApplicationContext context) {
        this.applicationContext = context;
        this.currentUserId = context.getSessionManager().getCurrentUserId();
        super.errorLabel = this.errorLabel;
    }

    @FXML
    private void handleSave(ActionEvent event) {
        clearError();

        String oldPw = oldPasswordField.getText().trim();
        String newPw = newPasswordField.getText().trim();
        String confirmPw = confirmPasswordField.getText().trim();

        if (oldPw.isEmpty() || newPw.isEmpty() || confirmPw.isEmpty()) {
            showError("Vui lòng điền đầy đủ các thông tin!");
            return;
        }

        if (!newPw.equals(confirmPw)) {
            showError("Mật khẩu mới không khớp!");
            return;
        }

        btnSave.setDisable(true);
        btnSave.setText("Đang xử lý...");

        Task<AuthResponse> changePwTask = new Task<>() {
            @Override
            protected AuthResponse call() {
                ChangePasswordRequest request = new ChangePasswordRequest(oldPw, newPw, confirmPw);
                return applicationContext.getAuthService().changePassword(currentUserId, request);
            }
        };

        changePwTask.setOnSucceeded(e -> {
            AuthResponse response = changePwTask.getValue();
            if (response.isSuccess()) {
                AlertUtils.showSuccess("Đổi mật khẩu thành công!");
                handleCancel();
            } else {
                showError(response.getResponse());
                resetButton();
            }
        });

        changePwTask.setOnFailed(e -> {
            showError("Lỗi hệ thống. Vui lòng thử lại sau.");
            resetButton();
        });

        new Thread(changePwTask).start();
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }

    private void resetButton() {
        btnSave.setDisable(false);
        btnSave.setText("Lưu thay đổi");
    }
}