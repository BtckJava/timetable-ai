package com.ocr.javafx.controller.views;

import com.ocr.javafx.ApplicationContext;
import com.ocr.javafx.controller.components.PlanCardController;
import com.ocr.javafx.dto.LearningPlanDTO;
import com.ocr.javafx.dto.response.LearningPlanResponse;
import com.ocr.javafx.service.LearningPlanService;
import javafx.application.Platform;
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

        // 🔥 USE MOCK DATA INSTEAD
        List<LearningPlanDTO> dtos = mockPlans;

        for (LearningPlanDTO dto : dtos) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ocr/javafx/components/plan-card.fxml"));
                VBox cardNode = loader.load();

                PlanCardController cardController = loader.getController();

                cardController.setPlanData(
                        dto,
                        applicationContext.getLearningPlanRepository(),
                        dto.getId(),
                        () -> {
                            System.out.println("Deleted plan: " + dto.getTitle());
                        },
                        () -> {
                            System.out.println("Đang gọi AI cho plan: " + dto.getTitle());
                        }
                );

                flowPanePlans.getChildren().add(cardNode);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
//    private void loadLearningPlans() {
//        flowPanePlans.getChildren().clear();
//
//        LearningPlanResponse response = planService.getAllPlans(currentUserId);
//
//        if (response.isSuccess() && response.getData() != null) {
//            @SuppressWarnings("unchecked")
//            List<LearningPlanDTO> dtos = (List<LearningPlanDTO>) response.getData();
//            long mockPlanId = 1; //mock data
//
//            for (LearningPlanDTO dto : dtos) {
//                try {
//                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ocr/javafx/learningplan/plan-card.fxml"));
//                    VBox cardNode = loader.load();
//
//                    PlanCardController cardController = loader.getController();
//
//                    cardController.setPlanData(
//                            dto,
//                            applicationContext.getLearningPlanRepository(),          // ✅ must pass repo
//                            dto.getId(),         // or mockPlanId if needed
//                            () -> {
//                                System.out.println("Deleted plan: " + dto.getTitle());
//                            },
//                            () -> {
//                                System.out.println("Đang gọi AI cho plan: " + dto.getTitle());
//                            }
//                    );
//
//                    flowPanePlans.getChildren().add(cardNode);
//
//                } catch (IOException e) {
//                    System.err.println("Lỗi khi load giao diện PlanCard.fxml: " + e.getMessage());
//                    e.printStackTrace();
//                }
//            }
//        } else {
//            showAlert(Alert.AlertType.ERROR, "Lỗi Tải Dữ Liệu", "Không thể lấy danh sách kế hoạch học tập từ Database.");
//        }
//    }

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