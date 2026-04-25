package com.ocr.javafx.controller.views;

import com.ocr.javafx.ApplicationContext;
import com.ocr.javafx.controller.main.MainController;
import com.ocr.javafx.dto.request.LearningPlanRequest;
import com.ocr.javafx.dto.response.LearningPlanResponse;
import com.ocr.javafx.entity.LearningPlan;
import com.ocr.javafx.entity.User;
import com.ocr.javafx.repository.LearningPlanRepository;
import com.ocr.javafx.repository.UserRepository;
import com.ocr.javafx.service.LearningPlanService;
import com.ocr.javafx.util.Function.AlertUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class NewLearningPlanController implements Initializable {

    private LearningPlanService planService;
    private ApplicationContext applicationContext;
    private Long currentUserId;
    private UserRepository userRepository;

    @FXML
    private TextField txtTitle, txtDomain;
    @FXML
    private TextArea txtGoal;
    @FXML
    private Label lblDuration;
    @FXML
    private ComboBox<String> comboIntensity;
    @FXML
    private Button btnBack;

    private int duration = 10;
    private MainController mainController;

    public void init(ApplicationContext context) {
        this.applicationContext = context;
        this.planService = context.getLearningPlanService();
        this.currentUserId = context.getSessionManager().getCurrentUserId();
        this.userRepository = context.getUserRepository();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        comboIntensity.setItems(FXCollections.observableArrayList("Low", "Medium", "High"));
        comboIntensity.getSelectionModel().select("Medium");
        lblDuration.setText(String.valueOf(duration));
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void incrementDays() { lblDuration.setText(String.valueOf(++duration)); }
    @FXML
    private void decrementDays() { if(duration > 1) lblDuration.setText(String.valueOf(--duration)); }

    @FXML
    private void handleCreatePlan() {
        if (txtTitle.getText().trim().isEmpty() || txtDomain.getText().trim().isEmpty()) {
            AlertUtils.showAlert(Alert.AlertType.WARNING, "Validation Error", "Title and Domain cannot be empty!");
            return;
        }
        User currentUser = userRepository.findById(currentUserId);
        LearningPlanRequest request = new LearningPlanRequest(
                txtTitle.getText().trim(),
                txtGoal.getText().trim(),
                comboIntensity.getValue(),
                new ArrayList<>(),
                txtDomain.getText().trim(),
                duration
        );
        LearningPlanResponse response = planService.createLearningPlan(currentUser, request);
        if (response.isSuccess()) {
            AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Success", "Learning Plan created successfully!");
            handleBack();
        } else {
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", response.getResponse());
        }
    }

    @FXML
    private void handleBack() {
        if (mainController != null) {
            mainController.setContent("/com/ocr/javafx/views/learning-plan-view.fxml");
        } else {
            System.err.println("MainController is null! Không thể quay lại.");
        }
    }
}