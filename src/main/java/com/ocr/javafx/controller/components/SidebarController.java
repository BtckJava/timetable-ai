package com.ocr.javafx.controller.components;

import com.ocr.javafx.controller.main.MainController;
import javafx.event.ActionEvent;
import lombok.Setter;

public class SidebarController {
    @Setter
    private MainController mainController;

    public void handleDashboard(ActionEvent actionEvent){
        mainController.setContent("/com/ocr/javafx/views/dashboard.fxml");
    }

    public void handleTimetable(ActionEvent actionEvent){
//        mainController.setContent("/com/ocr/javafx/views/timetable.fxml");
    }

    public void handleLearningPlans(ActionEvent actionEvent) {
        mainController.setContent("/com/ocr/javafx/views/learningPlans.fxml");
    }

    public void handleProfile(ActionEvent actionEvent){
        mainController.setContent("/com/ocr/javafx/views/profile.fxml");
    }
}
