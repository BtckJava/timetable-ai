package com.ocr.javafx.controller;

import javafx.scene.control.Label;

public class BaseController {

    protected Label errorLabel;

    protected void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }

    protected void showSuccess(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setStyle("-fx-text-fill: green;");
        }
    }

    protected void clearError() {
        if (errorLabel != null) {
            errorLabel.setText("");
        }
    }
}