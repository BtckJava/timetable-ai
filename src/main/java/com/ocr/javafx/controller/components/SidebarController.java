package com.ocr.javafx.controller.components;

import com.ocr.javafx.controller.main.MainController;
import com.ocr.javafx.enums.View;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import lombok.Setter;

public class SidebarController {
    @Setter
    private MainController mainController;

    @FXML
    private Button dashboardBtn;

    @FXML
    private Button timetableBtn;

    @FXML
    private Button learningPlansBtn;

    @FXML
    private Button profileBtn;

    public void handleDashboard(ActionEvent actionEvent){
        if(mainController != null)
            mainController.setContent("/com/ocr/javafx/views/dashboard.fxml", View.DASHBOARD);
    }

    public void handleTimetable(ActionEvent actionEvent) {
        if (mainController != null) {
            mainController.setContent("/com/ocr/javafx/timetable/Timetable.fxml", View.TIMETABLE);
        }
    }

    public void handleLearningPlans(ActionEvent actionEvent) {
        if(mainController != null)
            mainController.setContent("/com/ocr/javafx/views/learning-plan-view.fxml", View.LEARNING_PLANS);
    }

    public void handleProfile(ActionEvent actionEvent){
        if(mainController != null)
            mainController.setContent("/com/ocr/javafx/views/profile.fxml", View.PROFILE);
    }

    public void updateActive(View view) {

        // reset all
        dashboardBtn.getStyleClass().remove("sidebar-button-active");
        timetableBtn.getStyleClass().remove("sidebar-button-active");
        learningPlansBtn.getStyleClass().remove("sidebar-button-active");
        profileBtn.getStyleClass().remove("sidebar-button-active");

        // set active
        switch (view) {
            case DASHBOARD:
                dashboardBtn.getStyleClass().add("sidebar-button-active");
                break;
            case TIMETABLE:
                timetableBtn.getStyleClass().add("sidebar-button-active");
                break;
            case LEARNING_PLANS:
                learningPlansBtn.getStyleClass().add("sidebar-button-active");
                break;
            case PROFILE:
                profileBtn.getStyleClass().add("sidebar-button-active");
                break;
        }
    }
}
