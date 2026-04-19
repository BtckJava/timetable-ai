package com.ocr.javafx.controller.learningplan;

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
    private Button btnDelete;
    @FXML
    private Button btnAiGenerate;
    @FXML
    private ProgressIndicator progressLoading;

    private LearningPlanRepository repository;
    private Long planId;
    private Runnable onDeleteCallback;

    public void setPlanData(LearningPlanDTO plan, LearningPlanRepository repository, Long planId, Runnable onDelete, Runnable onAiAction) {
        this.planId = plan.getId();
        this.repository = repository;
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
        if (plan.getSkills() != null) {
            for (String skill : plan.getSkills()) {
                Label skillLabel = new Label(skill);
                skillLabel.setStyle("-fx-background-color: #EFF6FF; -fx-text-fill: #3B82F6; -fx-padding: 4 8; -fx-background-radius: 4; -fx-font-size: 12px;");
                flowPaneSkills.getChildren().add(skillLabel);
            }
        }

        btnAiGenerate.setOnAction(e -> {
            if (onAiAction != null) {
                onAiAction.run();
            }
        });
    }

    public void setLoadingState(boolean isLoading) {
        btnAiGenerate.setDisable(isLoading);
        btnAiGenerate.setText(isLoading ? "Đang xử lý..." : "✨ Tạo lịch học bằng AI");
        progressLoading.setVisible(isLoading);
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        try {
            repository.deleteById(this.planId);

            if (onDeleteCallback != null) {
                onDeleteCallback.run();
            }
        } catch (Exception e) {
            AlertUtils.showError(e.getMessage());
        }
    }
}