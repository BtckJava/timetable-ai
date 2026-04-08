package com.ocr.javafx.controller.main;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

public class BarchartController {
    @FXML
    private BarChart<String, Number> barchart;

    public void setupBarchart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        series.getData().add(new XYChart.Data<>("Mon", 3));
        series.getData().add(new XYChart.Data<>("Tue", 4));
        series.getData().add(new XYChart.Data<>("Wed", 2.5));
        series.getData().add(new XYChart.Data<>("Thu", 5));
        series.getData().add(new XYChart.Data<>("Fri", 3.5));
        series.getData().add(new XYChart.Data<>("Sat", 6));
        series.getData().add(new XYChart.Data<>("Sun", 2));

        barchart.getData().add(series);
    }
}
