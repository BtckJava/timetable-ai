package com.ocr.javafx.controller;

import com.ocr.javafx.entity.ScheduleSlot;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.LocalTime;

public class TimetableController {

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
    private TableColumn<ScheduleSlot, String> planCol;

    @FXML
    private TableColumn<ScheduleSlot, Boolean> completedCol;

    private final ObservableList<ScheduleSlot> slots =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        topicCol.setCellValueFactory(new PropertyValueFactory<>("topic"));
        subTopicCol.setCellValueFactory(new PropertyValueFactory<>("subTopic"));

        completedCol.setCellValueFactory(
                new PropertyValueFactory<>("completed")
        );

        // map plan name
        planCol.setCellValueFactory(cell -> {

            if (cell.getValue().getPlan() == null)
                return new javafx.beans.property.SimpleStringProperty("");

            return new javafx.beans.property.SimpleStringProperty(
                    cell.getValue().getPlan().getGoal()
            );
        });

        table.setItems(slots);

        loadSampleData();
    }

    private void loadSampleData() {

        ScheduleSlot s1 = new ScheduleSlot();
        s1.setDate(LocalDate.now());
        s1.setStartTime(LocalTime.of(9,0));
        s1.setEndTime(LocalTime.of(10,30));
        s1.setTopic("Java");
        s1.setSubTopic("JPA");
        s1.setCompleted(false);

        ScheduleSlot s2 = new ScheduleSlot();
        s2.setDate(LocalDate.now());
        s2.setStartTime(LocalTime.of(14,0));
        s2.setEndTime(LocalTime.of(16,0));
        s2.setTopic("Algorithms");
        s2.setSubTopic("Graph");
        s2.setCompleted(false);

        slots.addAll(s1, s2);
    }
}