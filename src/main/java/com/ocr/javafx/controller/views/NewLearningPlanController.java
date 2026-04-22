package com.ocr.javafx.controller.views;

import com.ocr.javafx.dto.request.LearningPlanRequest;
import com.ocr.javafx.service.LearningPlanService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class NewLearningPlanController {
    @FXML private TextField txtTitle, txtDomain, txtSkillInput;
    @FXML private TextArea txtGoal;
    @FXML private Label lblDuration;
    @FXML private ComboBox<String> comboIntensity;
    @FXML private FlowPane flowPaneSkills;

    private int duration = 14;
    private List<String> skills = new ArrayList<>();

    private LearningPlanService planService;

    @FXML
    public void initialize() {
        comboIntensity.setItems(FXCollections.observableArrayList("Low", "Moderate", "High"));
        comboIntensity.setValue("Moderate");
    }

    @FXML private void incrementDays() { lblDuration.setText(String.valueOf(++duration)); }
    @FXML private void decrementDays() { if(duration > 1) lblDuration.setText(String.valueOf(--duration)); }

    @FXML private void addSkill() {
        String skill = txtSkillInput.getText().trim();
        if (!skill.isEmpty() && !skills.contains(skill)) {
            skills.add(skill);
            Button skillTag = new Button(skill + " ✕");
            skillTag.setStyle("-fx-background-color: #DBEAFE; -fx-text-fill: #1E40AF; -fx-background-radius: 15;");
            skillTag.setOnAction(e -> {
                flowPaneSkills.getChildren().remove(skillTag);
                skills.remove(skill);
            });
            flowPaneSkills.getChildren().add(skillTag);
            txtSkillInput.clear();
        }
    }

    @FXML private void handleCreatePlan() {
        // Gom data vào LearningPlanRequest rồi gọi service.createLearningPlan
        // Sau đó đóng stage: ((Stage)txtTitle.getScene().getWindow()).close();
    }
}