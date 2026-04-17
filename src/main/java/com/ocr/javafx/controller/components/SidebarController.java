package com.ocr.javafx.controller.components;

import com.ocr.javafx.controller.main.MainController;
import javafx.event.ActionEvent;
import lombok.Setter;

public class SidebarController {
    @Setter
    private MainController mainController;

    public void onActionDashboard(ActionEvent actionEvent){
        mainController.setContent("/com/ocr/javafx/views/dashboard.fxml");
    }

    public void onActionTimetable(ActionEvent actionEvent){
//        mainController.setContent("/com/ocr/javafx/views/timetable.fxml");
    }

    public void onActionLearningPlans(ActionEvent actionEvent) {
//        mainController.setContent("/com/ocr/javafx/views/learningPlans.fxml");
    }

    public void onActionProfile(ActionEvent actionEvent){
        mainController.setContent("/com/ocr/javafx/views/profile.fxml");
    }
}
