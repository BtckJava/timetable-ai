package com.ocr.javafx.controller.components;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class TopbarController {
    @FXML
    public Button minBtn;

    @FXML
    public Button maxBtn;

    @FXML
    public Button exitBtn;

    @FXML
    private HBox topbar;

    @FXML
    private void handleMinimize() {
        Stage stage = (Stage) topbar.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleMaximize() {
        Stage stage = (Stage) topbar.getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }

    @FXML
    private void handleExit() {
        Stage stage = (Stage) topbar.getScene().getWindow();
        stage.close();
    }
}
