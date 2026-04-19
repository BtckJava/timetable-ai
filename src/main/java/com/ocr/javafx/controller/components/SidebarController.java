package com.ocr.javafx.controller.components;

import com.ocr.javafx.controller.main.MainController;
import javafx.fxml.FXML;
import lombok.Setter;

public class SidebarController {

    @Setter
    private MainController mainController;

    @FXML
    private void onDashboard() {
        if (mainController != null) {
            mainController.showDashboard();
        }
    }

    @FXML
    private void onTimetable() {
        if (mainController != null) {
            mainController.showTimetableAi();
        }
    }
}
