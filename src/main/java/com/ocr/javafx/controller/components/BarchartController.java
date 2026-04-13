package com.ocr.javafx.controller.components;

import com.ocr.javafx.ApplicationContext;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import lombok.Setter;

import java.util.Map;

public class BarchartController {

    @FXML
    private BarChart<String, Number> barchart;

    @Setter
    private ApplicationContext applicationContext;

    public void setupBarchart() {

        Long userId = applicationContext
                .getSessionManager()
                .getCurrentUser()
                .getId();

        Map<String, Double> data =
                applicationContext
                        .getLearningSessionService()
                        .getLearningHoursByDay(userId);

        barchart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Learning Hours");

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            series.getData().add(
                    new XYChart.Data<>(entry.getKey(), entry.getValue())
            );
        }

        barchart.getData().add(series);
    }
}