package com.ocr.javafx.controller.components;

import com.ocr.javafx.ApplicationContext;
import com.ocr.javafx.service.StatsRowService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class StatsRowController {

    @FXML
    private Label learningHoursLabel;

    @FXML
    private Label completedPlansLabel;

    @FXML
    private Label inProgressLabel;

    @FXML
    private Label progressLabel;

    private ApplicationContext applicationContext;
    private StatsRowService statsRowService;

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.statsRowService = applicationContext.getStatsRowService();

        loadStats();
    }

    public void loadStats() {
        Long userId = applicationContext
                .getSessionManager()
                .getCurrentUser()
                .getId();

        double hours = statsRowService.getTotalLearningHours(userId);
        long completed = statsRowService.getCompletedPlans(userId);
        long inProgress = statsRowService.getInProgressPlans(userId);
        double progress = statsRowService.getProgress(userId);

        learningHoursLabel.setText(String.format("%.1f", hours));
        completedPlansLabel.setText(String.valueOf(completed));
        inProgressLabel.setText(String.valueOf(inProgress));
        progressLabel.setText(String.format("%.0f%%", progress));
    }

//    @FXML
//    public void initialize() {
//        System.out.println(learningHoursLabel);
//    }
}