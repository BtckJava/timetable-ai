package com.ocr.javafx.controller.views;

import com.ocr.javafx.controller.components.PlanCardController;
import com.ocr.javafx.dto.LearningPlanDTO;
import com.ocr.javafx.repository.LearningPlanRepository;
import com.ocr.javafx.repository.ScheduleSlotRepository;
import com.ocr.javafx.service.LearningPlanService;
import com.ocr.javafx.service.ScheduleSlotService;
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

//    private final LearningPlanController learningPlanController;
    @FXML
    private FlowPane flowPanePlans;

    private LearningPlanService planService;

//    public LearningPlanController(LearningPlanController learningPlanController) {
//        this.learningPlanController = learningPlanController;
//    }

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

//        //hardcode
//        Long CURRENT_USER_ID = 1L;
//        LearningPlanResponse response = planService.getAllPlans(CURRENT_USER_ID);
//
//        if (response.isSuccess() && response.getData() != null) {
//            @SuppressWarnings("unchecked")
//            List<LearningPlanDTO> dtos = (List<LearningPlanDTO>) response.getData();
//            long mockPlanId = 1; //mock data

        long mockPlanId = 1;

        List<LearningPlanDTO> dtos = List.of(
                new LearningPlanDTO(
                        "Java Basics",
                        "Programming",
                        "Learn core Java",
                        40,
                        30,
                        "Medium",
                        List.of("OOP", "Collections"),
                        18,
                        "2026-04-01"
                ),
                new LearningPlanDTO(
                        "Data Structures",
                        "CS",
                        "Master DSA",
                        20,
                        60,
                        "Hard",
                        List.of("Trees", "Graphs"),
                        50,
                        "2026-03-20"
                )
        );


        for (LearningPlanDTO dto : dtos) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ocr/javafx/components/plan-card.fxml"));
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
    }
//    else{
//        learningPlanController.showAlert(Alert.AlertType.ERROR, "Lỗi Tải Dữ Liệu", "Không thể lấy danh sách kế hoạch học tập từ Database.");
//    }
}
