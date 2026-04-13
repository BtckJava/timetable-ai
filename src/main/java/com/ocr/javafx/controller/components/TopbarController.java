package com.ocr.javafx.controller.components;

import com.ocr.javafx.ApplicationContext;
import com.ocr.javafx.controller.main.MainController;
import com.ocr.javafx.entity.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import lombok.Setter;

public class TopbarController {
    @ FXML
    public Label nameLabel;

    @FXML
    public Label emailLabel;

    @Setter
    private ApplicationContext applicationContext;

    @Setter
    private MainController mainController;

    @FXML
    public Button minBtn;

    @FXML
    public Button maxBtn;

    @FXML
    public Button exitBtn;

    @FXML
    private HBox topbar;

    @FXML
    private ImageView profileImage;

    private final Popup popup = new Popup();

    private final Label name = new Label();
    private final Label email = new Label();

    public void setUser(User user) {
        name.setText(user.getUsername());
        email.setText(user.getEmail());
    }

    private VBox createPopupContent() {
        VBox box = new VBox();
        box.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 10;");
        box.setSpacing(5);

        box.getChildren().addAll(name, email);
        return box;
    }

    @FXML
    public void initialize() {
        VBox content = createPopupContent();
        popup.getContent().add(content);

        profileImage.setOnMouseEntered(e -> {
            if (!popup.isShowing()) {
                double x = profileImage.localToScreen(0, 0).getX();
                double y = profileImage.localToScreen(0, 0).getY();

                popup.show(profileImage, x - 100, y + 40); // offset
            }
        });

        profileImage.setOnMouseExited(e -> popup.hide());
    }

    @FXML
    private void handleMinimize() {
        Stage stage = (Stage) topbar.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleMaximize() {
        Stage stage = (Stage) topbar.getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }

    @FXML
    private void handleExit() {
        Stage stage = (Stage) topbar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void sidebarToggle(){
        mainController.sidebarToggle();
    }
}
