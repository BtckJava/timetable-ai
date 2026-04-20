package com.ocr.javafx.util.Function;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class AlertUtils {

    public static void showAlert(Alert.AlertType type, String title, String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    public static void showSuccess(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Thành công", null, message);
    }

    public static void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Đã có lỗi xảy ra", message);
    }
}