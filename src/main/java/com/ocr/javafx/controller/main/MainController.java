package com.ocr.javafx.controller.main;

import com.ocr.javafx.ApplicationContext;
import com.ocr.javafx.controller.components.BarchartController;
import com.ocr.javafx.controller.components.SidebarController;
import com.ocr.javafx.controller.components.StatsRowController;
import com.ocr.javafx.controller.components.TopbarController;
import com.ocr.javafx.controller.timetable.TimetableController;
import com.ocr.javafx.entity.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainController {
    @FXML
    private AnchorPane sidebar;

    @FXML
    public StackPane barchart;

    @FXML
    private StackPane mainContentStack;

    @FXML
    private VBox dashboardRoot;

    @FXML
    private TopbarController topbarController;

    @FXML
    private SidebarController sidebarController;

    @FXML
    private BarchartController barchartController;

    @FXML
    private StatsRowController statsRowController;

    private Node timetableAiRoot;

    private TimetableController timetableAiController;

    private ApplicationContext applicationContext;

    public void init(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        User user = applicationContext.getSessionManager().getCurrentUser();

        // Topbar
        topbarController.setApplicationContext(applicationContext);
        topbarController.setMainController(this);
        topbarController.setUser(user);

        sidebarController.setMainController(this);

        // Stats
        statsRowController.setApplicationContext(applicationContext);

        // Chart
        barchartController.setApplicationContext(applicationContext);
        barchartController.setupBarchart();
    }

    public void showTimetableAi() {
        try {
            ensureTimetableAiLoaded();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (timetableAiController != null) {
            timetableAiController.refreshFromDatabase();
        }
        dashboardRoot.setVisible(false);
        dashboardRoot.setManaged(false);
        timetableAiRoot.setVisible(true);
        timetableAiRoot.setManaged(true);
    }

    public void showDashboard() {
        if (timetableAiRoot != null) {
            timetableAiRoot.setVisible(false);
            timetableAiRoot.setManaged(false);
        }
        dashboardRoot.setVisible(true);
        dashboardRoot.setManaged(true);
    }

    private void ensureTimetableAiLoaded() throws IOException {
        if (timetableAiRoot != null) {
            return;
        }
        FXMLLoader loader = new FXMLLoader(
                MainController.class.getResource("/com/ocr/javafx/timetable/TimetableAI.fxml"));
        timetableAiRoot = loader.load();
        timetableAiController = loader.getController();
        timetableAiController.setApplicationContext(applicationContext);
        timetableAiRoot.setVisible(false);
        timetableAiRoot.setManaged(false);
        mainContentStack.getChildren().add(timetableAiRoot);
    }

    @FXML
    public void sidebarToggle() {
        boolean isVisible = sidebar.isVisible();

        sidebar.setVisible(!isVisible);
        sidebar.setManaged(!isVisible);
    }
}
