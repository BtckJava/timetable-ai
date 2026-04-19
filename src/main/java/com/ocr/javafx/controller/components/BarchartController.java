package com.ocr.javafx.controller.components;

import com.ocr.javafx.ApplicationContext;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import lombok.Setter;

import java.util.Map;

public class BarchartController {
    @FXML
    public CategoryAxis xAxis;

    @FXML
    public NumberAxis yAxis;

    @FXML
    private BarChart<String, Number> barchart;

    @Setter
    private ApplicationContext applicationContext;

    public void setupBarchart() {

        Long userId = applicationContext
                .getSessionManager()
                .getCurrentUser()
                .getId();

//        Map<String, Double> data =
//                applicationContext
//                        .getLearningSessionService()
//                        .getLearningHoursByDay(userId);

        Map<String, Double> data = Map.of(
                "Mon", 2.5,
                "Tue", 4.0,
                "Wed", 1.5,
                "Thu", 5.0,
                "Fri", 3.5,
                "Sat", 6.5,
                "Sun", 2.0
        );

        yAxis.setForceZeroInRange(true);
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(8);
        yAxis.setMinorTickVisible(false);
        yAxis.setTickUnit(2);

        barchart.setCategoryGap(10);  // default is usually ~20-40
        barchart.setBarGap(4);
        barchart.setPadding(Insets.EMPTY);

        barchart.setAnimated(true);
        barchart.setLegendVisible(false);
        xAxis.setLabel("");
        yAxis.setLabel("");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Weekly Learning Hours");

        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

        for (String day : days) {
            Double value = data.getOrDefault(day, 0.0);
            series.getData().add(new XYChart.Data<>(day, value));
        }

        barchart.getData().clear();
        barchart.getData().add(series);
    }
}