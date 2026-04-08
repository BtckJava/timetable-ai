package com.ocr.javafx.controller.main;

import com.ocr.javafx.controller.components.TopbarController;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class MainController {
    @FXML
    private AnchorPane sidebar;

    @FXML
    public StackPane barchart;

    @FXML
    private TopbarController topbarController;

    @FXML
    private BarchartController barchartController;

    @FXML
    public void initialize() {
        topbarController.setMainController(this);
        barchartController.setupBarchart();
    }

    @FXML
    public void sidebarToggle() {
        boolean isVisible = sidebar.isVisible();

        sidebar.setVisible(!isVisible);
        sidebar.setManaged(!isVisible);
    }
}
