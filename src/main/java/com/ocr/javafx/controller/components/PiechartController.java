package com.ocr.javafx.controller.components;

import com.ocr.javafx.ApplicationContext;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Tooltip;
import lombok.Setter;

import java.util.Map;

public class PiechartController {

    @FXML
    private PieChart piechart;

    @Setter
    private ApplicationContext applicationContext;

    public void setupPieChart() {

        Long userId = applicationContext
                .getSessionManager()
                .getCurrentUser()
                .getId();

        Map<String, Double> data = applicationContext
                .getLearningChartService()
                .getSkillDistribution(userId);

        piechart.getData().clear();

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            PieChart.Data slice = new PieChart.Data(
                    entry.getKey(),
                    entry.getValue()
            );

            piechart.getData().add(slice);

            slice.nodeProperty().addListener((obs, oldNode, node) -> {
                if (node != null) {
                    Tooltip.install(
                            node,
                            new Tooltip(entry.getKey() + ": " + entry.getValue())
                    );
                }
            });
        }

        piechart.setLabelsVisible(true);
        piechart.setLegendVisible(false);
        piechart.setClockwise(true);
        piechart.setStartAngle(90);

        piechart.setMinSize(300, 300);
        piechart.setPrefSize(300, 300);
        piechart.setMaxSize(330, 330);
    }
}