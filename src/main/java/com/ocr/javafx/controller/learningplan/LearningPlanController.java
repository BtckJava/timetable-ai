package com.ocr.javafx.controller.learningplan;

import com.ocr.javafx.dto.LearningPlanDTO;
import com.ocr.javafx.dto.response.LearningPlanResponse;
import com.ocr.javafx.repository.LearningPlanRepository;
import com.ocr.javafx.repository.ScheduleSlotRepository;
import com.ocr.javafx.service.LearningPlanService;
import com.ocr.javafx.service.ScheduleSlotService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LearningPlanController implements Initializable {

    @FXML
    private FlowPane flowPanePlans;

    private LearningPlanService planService;

    private final Long CURRENT_USER_ID = 1L; //hardcode

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LearningPlanRepository planRepo = new LearningPlanRepository();
        ScheduleSlotRepository slotRepo = new ScheduleSlotRepository();

        ScheduleSlotService slotService = new ScheduleSlotService(slotRepo);

        this.planService = new LearningPlanService(planRepo, slotService);

        loadLearningPlans();
    }

    private void loadLearningPlans() {
        flowPanePlans.getChildren().clear();

        LearningPlanResponse response = planService.getAllPlans(CURRENT_USER_ID);

        if (response.isSuccess() && response.getData() != null) {
            @SuppressWarnings("unchecked")
            List<LearningPlanDTO> dtos = (List<LearningPlanDTO>) response.getData();
            long mockPlanId = 1; //mock data

            for (LearningPlanDTO dto : dtos) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ocr/javafx/learningplan/plan-card.fxml"));
                    VBox cardNode = loader.load();

                    PlanCardController cardController = loader.getController();

                    cardController.setPlanData(dto, mockPlanId, () -> {
                        System.out.println("Đang gọi AI cho plan: " + dto.getTitle());
                    });

                    flowPanePlans.getChildren().add(cardNode);

                } catch (IOException e) {
                    System.err.println("Lỗi khi load giao diện PlanCard.fxml: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi Tải Dữ Liệu", "Không thể lấy danh sách kế hoạch học tập từ Database.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}