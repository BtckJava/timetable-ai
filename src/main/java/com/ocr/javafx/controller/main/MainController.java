package com.ocr.javafx.controller.main;

import com.ocr.javafx.ApplicationContext;
import com.ocr.javafx.controller.components.SidebarController;
import com.ocr.javafx.controller.components.TopbarController;
import com.ocr.javafx.controller.timetable.TimetableController;
import com.ocr.javafx.controller.views.DashboardController;
import com.ocr.javafx.entity.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

public class MainController {

    @FXML
    private SidebarController sidebarController;

    @FXML
    public ScrollPane contentPane;

    @FXML
    private AnchorPane sidebar;

    @FXML
    private TopbarController topbarController;

    private ApplicationContext applicationContext;

    public void init(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        User user = applicationContext.getSessionManager().getCurrentUser();

        // Topbar
        topbarController.setApplicationContext(applicationContext);
        topbarController.setMainController(this);
        topbarController.setUser(user);

        // Sidebar
        sidebarController.setMainController(this);

        setContent("/com/ocr/javafx/views/dashboard.fxml");
    }

    @FXML
    public void sidebarToggle() {
        boolean isVisible = sidebar.isVisible();

        sidebar.setVisible(!isVisible);
        sidebar.setManaged(!isVisible);
    }

    public void setContent(String path) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            contentPane.setContent(loader.load());

            Object controller = loader.getController();

            if (controller instanceof DashboardController) {
                ((DashboardController) controller).init(applicationContext);
            } else if (controller instanceof TimetableController) {
                ((TimetableController) controller).setApplicationContext(applicationContext);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SidebarController getSidebarController() {
        return sidebarController;
    }

    public void setSidebarController(SidebarController sidebarController) {
        this.sidebarController = sidebarController;
    }
}
