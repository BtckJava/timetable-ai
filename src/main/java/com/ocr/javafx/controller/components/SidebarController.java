package com.ocr.javafx.controller.components;

import com.ocr.javafx.controller.main.MainController;
import javafx.fxml.FXML;

public class SidebarController {

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void handleDashboard() {
        if (mainController != null) {
            mainController.setContent("/com/ocr/javafx/views/dashboard.fxml");
        }
    }

    @FXML
    public void handleTimetable() {
        if (mainController != null) {
            System.out.println("Chuyển sang màn Timetable");
        }
    }

    @FXML
    public void handleLearningPlans() {
        if (mainController != null) {
            mainController.setContent("/com/ocr/javafx/learningplan/learning-plan-view.fxml");
        }
    }

    @FXML
    public void handleProfile() {
        if (mainController != null) {
            System.out.println("Chuyển sang màn Profile");
        }
    }

}
