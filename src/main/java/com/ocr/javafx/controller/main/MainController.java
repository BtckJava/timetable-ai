package com.ocr.javafx.controller.main;

import com.ocr.javafx.controller.components.TopbarController;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class MainController {
    @FXML
    private AnchorPane sidebar;

    @FXML
    private TopbarController topbarController;

    @FXML
    public void initialize() {
        topbarController.setMainController(this);
    }

    @FXML
    public void sidebarToggle() {
        boolean isVisible = sidebar.isVisible();

        sidebar.setVisible(!isVisible);
        sidebar.setManaged(!isVisible);
    }
}
