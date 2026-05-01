package com.ocr.javafx.controller.views;

import com.ocr.javafx.ApplicationContext;
import com.ocr.javafx.controller.main.MainController;
import com.ocr.javafx.entity.LearningPlan;
import com.ocr.javafx.entity.ScheduleSlot;
import com.ocr.javafx.service.LearningPlanService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class PlanDetailsController {

    @FXML
    private Label lblTitle;
    @FXML
    private Label lblDomain;

    @FXML
    private TableView<ScheduleSlot> tableSlots;
    @FXML
    private TableColumn<ScheduleSlot, String> colDate;
    @FXML
    private TableColumn<ScheduleSlot, String> colTime;
    @FXML
    private TableColumn<ScheduleSlot, String> colTopic;
    @FXML
    private TableColumn<ScheduleSlot, String> colSubTopic;
    @FXML
    private TableColumn<ScheduleSlot, String> colStatus;

    private ApplicationContext applicationContext;
    private MainController mainController;
    private LearningPlanService planService;

    public void init(ApplicationContext context, MainController mainController, Long planId) {
        this.applicationContext = context;
        this.mainController = mainController;
        this.planService = context.getLearningPlanService();

        setupTableColumns();
        loadPlanData(planId);
    }

    private void setupTableColumns() {
        colDate.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDate() != null ? cellData.getValue().getDate().toString() : "N/A"));

        colTime.setCellValueFactory(cellData -> {
            ScheduleSlot slot = cellData.getValue();
            String time = (slot.getStartTime() != null ? slot.getStartTime().toString() : "") + " - " +
                    (slot.getEndTime() != null ? slot.getEndTime().toString() : "");
            return new SimpleStringProperty(time);
        });

        colTopic.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTopic()));
        colSubTopic.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSubTopic()));
        colStatus.setCellValueFactory(cellData -> {
            boolean isDone = Boolean.TRUE.equals(cellData.getValue().isCompleted());
            return new SimpleStringProperty(isDone ? "Đã xong" : "Chưa học");
        });

        colStatus.setCellFactory(column -> new TableCell<ScheduleSlot, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label badgeLabel = new Label(item);
                    badgeLabel.getStyleClass().removeAll("badge", "status-done", "status-pending");
                    badgeLabel.getStyleClass().add("badge");
                    if (item.equals("Đã xong")) {
                        badgeLabel.getStyleClass().add("status-done");
                    } else {
                        badgeLabel.getStyleClass().add("status-pending");
                    }
                    this.setAlignment(javafx.geometry.Pos.CENTER);
                    setGraphic(badgeLabel);
                    setText(null);
                }
            }
        });
    }

    private void loadPlanData(Long planId) {
        LearningPlan plan = planService.getPlanDetails(planId);
        if (plan != null) {
            lblTitle.setText(plan.getTitle());
            lblDomain.setText(plan.getDomain() != null ? plan.getDomain() : "N/A");

            if (plan.getSlots() != null)
                tableSlots.setItems(FXCollections.observableArrayList(plan.getSlots()));
        }
    }

    @FXML
    private void handleBack() {
        if (mainController != null) {
            mainController.setContent("/com/ocr/javafx/views/learning-plan-view.fxml");
        }
    }
}