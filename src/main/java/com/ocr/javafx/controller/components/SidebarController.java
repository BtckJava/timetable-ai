package com.ocr.javafx.controller.components;

import com.ocr.javafx.controller.main.MainController;
import javafx.event.ActionEvent;
import lombok.Setter;

public class SidebarController {
    @Setter
    private MainController mainController;

    public void handleDashboard(ActionEvent actionEvent){
        if(mainController != null)
            mainController.setContent("/com/ocr/javafx/views/dashboard.fxml");
    }

    public void handleTimetable(ActionEvent actionEvent){
//        if(mainController != null) mainController.setContent("/com/ocr/javafx/views/timetable.fxml");
    }

    public void handleLearningPlans(ActionEvent actionEvent) {
        if(mainController != null)
            mainController.setContent("/com/ocr/javafx/views/learning-plan-view.fxml");
    }

    public void handleProfile(ActionEvent actionEvent){
        if(mainController != null)
            mainController.setContent("/com/ocr/javafx/views/profile.fxml");
    }
}
