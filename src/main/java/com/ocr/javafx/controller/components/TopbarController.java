package com.ocr.javafx.controller.components;

import com.ocr.javafx.ApplicationContext;
import com.ocr.javafx.controller.main.MainController;
import com.ocr.javafx.entity.User;
import com.ocr.javafx.enums.View;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import lombok.Setter;

public class TopbarController {
    @FXML
    public Label currentViewLabel;

    @FXML
    public ImageView creditBtn;

    @Setter
    private ApplicationContext applicationContext;

    @FXML
    private ImageView profileImage;

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

    private StackPane creditOverlay;

    private final Popup popup = new Popup();

    private final Label name = new Label();
    private final Label email = new Label();

    private View currentView;

    public void setUser(User user) {
        name.setText(user.getUsername());
        email.setText(user.getEmail());

        Image image;

        if (user.getAvatarPath() != null && !user.getAvatarPath().isEmpty()) {
            image = new Image("file:" + user.getAvatarPath());
        } else {
            image = new Image(
                    getClass().getResource("/com/ocr/javafx/image/defaultProfileIcon.png")
                            .toExternalForm()
            );
        }

        profileImage.setImage(image);

        // make it look better
        profileImage.setFitWidth(32);
        profileImage.setFitHeight(32);
        profileImage.setPreserveRatio(true);

        // circle avatar (nice UI)
        javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(16, 16, 16);
        profileImage.setClip(clip);
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
        // profile button
        setupPopup();
        setupProfileInteractions();

        // credit button
        setupCreditOverlay();
    }

    private void setupPopup() {
        VBox content = createPopupContent();
        popup.getContent().add(content);
    }

    private void setupProfileInteractions() {
        profileImage.setOnMouseEntered(e -> {
            if (!popup.isShowing()) {
                double x = profileImage.localToScreen(0, 0).getX();
                double y = profileImage.localToScreen(0, 0).getY();
                popup.show(profileImage, x - 100, y + 40);
            }
        });

        profileImage.setOnMouseExited(e -> popup.hide());

        profileImage.setOnMousePressed(e -> {
            mainController.setContent("/com/ocr/javafx/views/profile.fxml", View.PROFILE);
        });
    }

    private void setupCreditOverlay() {
        ImageView imageView = new ImageView(
                new Image(
                        getClass().getResource("/com/ocr/javafx/image/testCredit.jpg").toExternalForm()
                )
        );

        imageView.setPreserveRatio(true);
        imageView.setFitWidth(700); // adjust size

        // overlay background
        creditOverlay = new StackPane(imageView);
        creditOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.7);");

        // full screen
        creditOverlay.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // center image
        StackPane.setAlignment(imageView, javafx.geometry.Pos.CENTER);

        // click anywhere to close
        creditOverlay.setOnMouseClicked(e -> {
            ((StackPane) topbar.getScene().getRoot()).getChildren().remove(creditOverlay);
        });
    }

    @FXML
    private void handleCreditClick() {
        StackPane root = (StackPane) topbar.getScene().getRoot();

        if (!root.getChildren().contains(creditOverlay)) {
            root.getChildren().add(creditOverlay);
        }
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

    public void updateActive(View view){
        switch (view){
            case View.DASHBOARD -> currentViewLabel.setText("Dashboard");
            case View.TIMETABLE -> currentViewLabel.setText("Timetable");
            case View.LEARNING_PLANS -> currentViewLabel.setText("Learning Plans");
            case View.PROFILE -> currentViewLabel.setText("Profile");
        }
    }
}
