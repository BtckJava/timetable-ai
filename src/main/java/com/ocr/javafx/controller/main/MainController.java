package com.ocr.javafx.controller.main;

import com.ocr.javafx.ApplicationContext;
import com.ocr.javafx.controller.components.BarchartController;
import com.ocr.javafx.controller.components.StatsRowController;
import com.ocr.javafx.controller.components.TopbarController;
import com.ocr.javafx.entity.User;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import lombok.Setter;

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
    private StatsRowController statsRowController;

    public void init(ApplicationContext applicationContext) {
        User user = applicationContext.getSessionManager().getCurrentUser();

        // Topbar
        topbarController.setApplicationContext(applicationContext);
        topbarController.setMainController(this);
        topbarController.setUser(user);

        // Stats
        statsRowController.setApplicationContext(applicationContext);

        // Chart
        barchartController.setApplicationContext(applicationContext);
        barchartController.setupBarchart();
    }

    @FXML
    public void sidebarToggle() {
        boolean isVisible = sidebar.isVisible();

        sidebar.setVisible(!isVisible);
        sidebar.setManaged(!isVisible);
    }
}
