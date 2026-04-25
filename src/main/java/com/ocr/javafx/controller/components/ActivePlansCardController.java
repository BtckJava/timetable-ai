package com.ocr.javafx.controller.components;

import com.ocr.javafx.entity.LearningPlan;
import com.ocr.javafx.enums.LearningPlanStatus;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class ActivePlansCardController {

    @FXML
    private Label titleLabel;

    @FXML
    private Label goalLabel;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label statusLabel;

    public void setData(LearningPlan plan) {

        // Title & goal
        titleLabel.setText(plan.getTitle());
        goalLabel.setText(plan.getGoal());

        // Progress (convert % → 0.0–1.0)
        double progress = plan.getProgress() / 100.0;
        progressBar.setProgress(progress);

        // Status text
        String statusText = plan.getStatus().name().replace("_", " ");
        statusLabel.setText(statusText);

        // Optional: color based on status
        applyStatusStyle(plan.getStatus());
    }

    private void applyStatusStyle(LearningPlanStatus status) {
        switch (status) {
            case IN_PROGRESS -> statusLabel.setStyle("-fx-text-fill: #00ffcc;");
            case COMPLETED -> statusLabel.setStyle("-fx-text-fill: #4CAF50;");
            case NOT_STARTED -> statusLabel.setStyle("-fx-text-fill: #aaaaaa;");
        }
    }
}