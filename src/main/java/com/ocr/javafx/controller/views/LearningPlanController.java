package com.ocr.javafx.controller.views;

import com.ocr.javafx.ApplicationContext;
import com.ocr.javafx.controller.components.PlanCardController;
import com.ocr.javafx.controller.main.MainController;
import com.ocr.javafx.dto.LearningPlanDTO;
import com.ocr.javafx.dto.response.LearningPlanResponse;
import com.ocr.javafx.service.LearningPlanService;
import com.ocr.javafx.util.Function.AlertUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LearningPlanController implements Initializable {

    @FXML
    private FlowPane flowPanePlans;

    @Setter
    private MainController mainController;



    private LearningPlanService planService;
    private ApplicationContext applicationContext;
    private Long currentUserId;

    List<LearningPlanDTO> mockPlans = List.of(
            new LearningPlanDTO(1L, "Java Backend", "Backend", "Build API", 40, 30, "High",
                    List.of("Java", "Spring"), 18, "2026-04-10"),

            new LearningPlanDTO(2L, "Frontend React", "Frontend", "Build UI", 70, 20, "Medium",
                    List.of("React", "JS"), 6, "2026-04-01"),

            new LearningPlanDTO(3L, "Data Structures", "CS", "Master DSA", 20, 45, "Low",
                    List.of("Arrays", "Trees"), 35, "2026-04-05")
    );

    public void init(ApplicationContext context) {
        this.applicationContext = context;
        this.planService = context.getLearningPlanService();
        this.currentUserId = context.getSessionManager().getCurrentUserId();
        loadLearningPlans();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    private void loadLearningPlans() {
        flowPanePlans.getChildren().clear();

        LearningPlanResponse response = planService.getAllPlans(currentUserId);

        if (response.isSuccess() && response.getData() != null) {
            @SuppressWarnings("unchecked")
            List<LearningPlanDTO> dtos = (List<LearningPlanDTO>) response.getData();

            for (LearningPlanDTO dto : dtos) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ocr/javafx/components/plan-card.fxml"));
                    VBox cardNode = loader.load();

                    PlanCardController cardController = loader.getController();

                    cardController.setPlanData(
                            dto,
                            applicationContext.getLearningPlanService(),
                            dto.getId(),
                            () -> { flowPanePlans.getChildren().remove(cardNode); },
                            () -> { showPlanDetails(dto.getId()); }
                    );

                    flowPanePlans.getChildren().add(cardNode);

                } catch (IOException e) {
                    System.err.println("Lỗi khi load giao diện PlanCard.fxml: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            AlertUtils.showError("Không thể lấy danh sách kế hoạch học tập từ Database.");
        }
    }
    @FXML
    private void handleNewPlan() {
        try {
            if(mainController != null)
                mainController.setContent("/com/ocr/javafx/views/new-learning-plan.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showError("Không thể mở màn hình tạo kế hoạch: " + e.getMessage());        }
    }

    private void showPlanDetails(Long planId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ocr/javafx/views/plan-details.fxml"));
            Parent root = loader.load();
            PlanDetailsController detailsController = loader.getController();
            detailsController.init(this.applicationContext, this.mainController, planId);
            if (mainController != null) {
                mainController.contentPane.setContent(root);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showError("Lỗi khi mở màn hình chi tiết: " + e.getMessage());
        }
    }

}