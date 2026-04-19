package com.ocr.javafx.controller.timetable;

import com.ocr.javafx.ApplicationContext;
import com.ocr.javafx.entity.ScheduleSlot;
import com.ocr.javafx.entity.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TimetableController {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("H:mm");

    @FXML
    private DatePicker slotDatePicker;

    @FXML
    private TextField startTimeField;

    @FXML
    private TextField endTimeField;

    @FXML
    private TextField topicField;

    @FXML
    private TextField subTopicField;

    @FXML
    private TableView<ScheduleSlot> table;

    @FXML
    private TableColumn<ScheduleSlot, LocalDate> dateCol;

    @FXML
    private TableColumn<ScheduleSlot, LocalTime> startCol;

    @FXML
    private TableColumn<ScheduleSlot, LocalTime> endCol;

    @FXML
    private TableColumn<ScheduleSlot, String> topicCol;

    @FXML
    private TableColumn<ScheduleSlot, String> subTopicCol;

    @FXML
    private TableColumn<ScheduleSlot, Boolean> completedCol;

    private final ObservableList<ScheduleSlot> slots = FXCollections.observableArrayList();

    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        refreshFromDatabase();
    }

    public void refreshFromDatabase() {
        if (applicationContext == null) {
            return;
        }
        User user = applicationContext.getSessionManager().getCurrentUser();
        if (user == null || user.getId() == null) {
            slots.clear();
            return;
        }
        slots.setAll(
                applicationContext.getScheduleSlotRepository()
                        .findByUserIdOrderByDateAndStart(user.getId()));
    }

    @FXML
    public void initialize() {
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        topicCol.setCellValueFactory(new PropertyValueFactory<>("topic"));
        subTopicCol.setCellValueFactory(new PropertyValueFactory<>("subTopic"));
        completedCol.setCellValueFactory(new PropertyValueFactory<>("completed"));

        table.setItems(slots);

        if (slotDatePicker != null) {
            slotDatePicker.setValue(LocalDate.now());
        }
    }

    @FXML
    private void onRefresh() {
        refreshFromDatabase();
    }

    @FXML
    private void onAddSlot() {
        if (applicationContext == null) {
            return;
        }
        User user = applicationContext.getSessionManager().getCurrentUser();
        if (user == null || user.getId() == null) {
            alert(Alert.AlertType.WARNING, "Chưa đăng nhập", "Không thể thêm slot.");
            return;
        }

        LocalDate date = slotDatePicker.getValue();
        if (date == null) {
            alert(Alert.AlertType.WARNING, "Thiếu ngày", "Chọn ngày học.");
            return;
        }

        LocalTime start;
        LocalTime end;
        try {
            start = LocalTime.parse(startTimeField.getText().trim(), TIME_FMT);
            end = LocalTime.parse(endTimeField.getText().trim(), TIME_FMT);
        } catch (DateTimeParseException e) {
            alert(Alert.AlertType.ERROR, "Giờ không hợp lệ", "Dùng định dạng HH:mm (ví dụ 9:00 hoặc 14:30).");
            return;
        }

        if (!end.isAfter(start)) {
            alert(Alert.AlertType.WARNING, "Khung giờ sai", "Giờ kết thúc phải sau giờ bắt đầu.");
            return;
        }

        String topic = topicField.getText() != null ? topicField.getText().trim() : "";
        if (topic.isEmpty()) {
            alert(Alert.AlertType.WARNING, "Thiếu chủ đề", "Nhập chủ đề (topic).");
            return;
        }

        String subTopic = subTopicField.getText() != null ? subTopicField.getText().trim() : "";

        ScheduleSlot slot = new ScheduleSlot();
        slot.setDate(date);
        slot.setStartTime(start);
        slot.setEndTime(end);
        slot.setTopic(topic);
        slot.setSubTopic(subTopic);
        slot.setCompleted(false);
        slot.setUser(user);
        slot.setPlan(null);

        try {
            applicationContext.getScheduleSlotRepository().save(slot);
        } catch (RuntimeException e) {
            e.printStackTrace();
            alert(Alert.AlertType.ERROR, "Lỗi lưu", e.getMessage() != null ? e.getMessage() : "Không lưu được slot.");
            return;
        }

        subTopicField.clear();
        refreshFromDatabase();
    }

    private void alert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
