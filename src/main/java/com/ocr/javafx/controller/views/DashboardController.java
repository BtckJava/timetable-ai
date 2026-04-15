package com.ocr.javafx.controller.views;

import com.ocr.javafx.ApplicationContext;
import com.ocr.javafx.controller.components.BarchartController;
import com.ocr.javafx.controller.components.PiechartController;
import com.ocr.javafx.controller.components.StatsRowController;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class DashboardController {
    @FXML
    public AnchorPane barWrapper;

    @FXML
    public AnchorPane pieWrapper;

    @FXML
    public HBox rootHBox;

    @FXML
    private BarchartController barchartController;

    @FXML
    private StatsRowController statsRowController;

    @FXML
    private PiechartController piechartController;

    @FXML
    public VBox barchart;

    public void init(ApplicationContext applicationContext){
    // Stats
        statsRowController.setApplicationContext(applicationContext);

    // Chart
        barchartController.setApplicationContext(applicationContext);
        barchartController.setupBarchart();

        piechartController.setApplicationContext(applicationContext);
        piechartController.setupPieChart();
//        CHECK
//        System.out.println("Loading dashboard...");

        barWrapper.prefWidthProperty().bind(
                rootHBox.widthProperty().multiply(0.66)
        );
        pieWrapper.prefWidthProperty().bind(
                rootHBox.widthProperty().multiply(0.34)
        );
    }
}
