package com.ocr.javafx.controller.components;

import com.ocr.javafx.ApplicationContext;
import com.ocr.javafx.dto.LearningPlanDTO;
import com.ocr.javafx.repository.LearningPlanRepository;
import com.ocr.javafx.service.LearningPlanService;
import com.ocr.javafx.util.Function.AlertUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;

import javafx.event.ActionEvent;
public class PlanCardController {
    @FXML
    private Label lblTitle;
    @FXML
    private Label lblGoal;
    @FXML
    private Label lblProgressPercent;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label lblDuration;
    @FXML
    private Label lblRemaining;
    @FXML
    private Label lblStarted;
    @FXML
    private Label lblIntensity;
    @FXML
    private FlowPane flowPaneSkills;
    @FXML
    private Button btnViewDetails;

    private LearningPlanService planService;
    private Long planId;
    private Runnable onDeleteCallback;

    public void setPlanData(LearningPlanDTO plan, LearningPlanService planService, Long planId, Runnable onDelete, Runnable onViewDetails) {
        this.planId = plan.getId();
        this.planService = planService;
        this.onDeleteCallback = onDelete;

        lblTitle.setText(plan.getTitle());
        lblGoal.setText(plan.getGoal() != null ? plan.getGoal() : "No description provided.");
        lblDuration.setText(plan.getDurationDays() + " days");
        lblRemaining.setText(plan.getRemainingDays() + " days");
        lblStarted.setText(plan.getStartedDate());

        int progress = plan.getProgress() != null ? plan.getProgress() : 0;
        lblProgressPercent.setText(progress + "%");
        progressBar.setProgress(progress / 100.0);

        lblIntensity.getStyleClass().removeAll("intensity-high", "intensity-medium", "intensity-low");

        lblIntensity.setText(plan.getIntensity() + " Intensity");

        if ("High".equalsIgnoreCase(plan.getIntensity())) {
            lblIntensity.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #EF4444;");
        } else if ("Medium".equalsIgnoreCase(plan.getIntensity())) {
            lblIntensity.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #D97706;");
        } else {
            lblIntensity.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #22C55E;");
        }

        flowPaneSkills.getChildren().clear();
        if (plan.getSkills() != null && !plan.getSkills().isEmpty()) {
            for (String skill : plan.getSkills()) {
                Label skillLabel = new Label(skill);
                skillLabel.setStyle("-fx-background-color: #EFF6FF; -fx-text-fill: #3B82F6; -fx-padding: 4 8; -fx-background-radius: 4; -fx-font-size: 12px;");
                flowPaneSkills.getChildren().add(skillLabel);
            }
        } else {
            Label waitingLabel = new Label("Waiting for AI generation...");
            waitingLabel.setStyle("-fx-text-fill: #9CA3AF; -fx-font-style: italic; -fx-font-size: 12px;");
            flowPaneSkills.getChildren().add(waitingLabel);
        }

        btnViewDetails.setOnAction(e -> {
            if (onViewDetails != null) {
                onViewDetails.run();
            }
        });
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        try {
            planService.deletePlanById(this.planId);

            if (onDeleteCallback != null) {
                onDeleteCallback.run();
            }
        } catch (Exception e) {
            AlertUtils.showError(e.getMessage());
        }
    }
}