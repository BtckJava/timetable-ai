package com.ocr.javafx.controller.views;

import com.ocr.javafx.ApplicationContext;
import com.ocr.javafx.controller.components.BarchartController;
import com.ocr.javafx.controller.components.StatsRowController;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class DashboardController {
    @FXML
    private BarchartController barchartController;

    @FXML
    private StatsRowController statsRowController;

    @FXML
    public VBox barchart;

    public void init(ApplicationContext applicationContext){
    // Stats
        statsRowController.setApplicationContext(applicationContext);

    // Chart
        barchartController.setApplicationContext(applicationContext);
        barchartController.setupBarchart();

//        CHECK
//        System.out.println("Loading dashboard...");
    }
}
