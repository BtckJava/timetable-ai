package com.ocr.javafx.controller.views;

import com.ocr.javafx.dto.request.ProfileRequest;
import com.ocr.javafx.dto.response.ProfileResponse;
import com.ocr.javafx.service.ProfileService;
import com.ocr.javafx.service.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;

public class ProfileController {

    @FXML
    private Button btnEditProfile;

    @FXML
    private ImageView imgAva;

    @FXML
    private Label lblEmail;

    @FXML
    private Label lblHours;

    @FXML
    private TextField txtName;

    @FXML
    private Label lblPlans;

    @FXML
    private Label lblSkills;

    @FXML
    private Label lblStreak;

    @FXML
    private Label txtMaster;

    @FXML
    private Label txtQuickLearner;

    @FXML
    private Label txtStreak;

    private final ProfileService profileService = new ProfileService();

    private String selectedAvatarPath;

    @FXML
    public void initialize() {
        loadProfile();
    }

    private void loadProfile() {
        if (UserSession.getInstance() == null) return;

        Long currentUserId = UserSession.getInstance().getId();
        if (currentUserId == null) return;

        ProfileResponse res = profileService.getProfile(String.valueOf(currentUserId));
        if (res == null) return;

        // info
        txtName.setText(res.getUsername());
        lblEmail.setText(res.getEmail());

        // stats
        lblHours.setText(String.valueOf(res.getTotalHours()));
        lblPlans.setText(String.valueOf(res.getCompletedPlans()));
        lblStreak.setText(String.valueOf(res.getCurrentStreak()));
        lblSkills.setText(String.valueOf(res.getSkillsLearned()));

        // avatar
        if (res.getAvatarPath() != null) {
            Image image = new Image("file:" + res.getAvatarPath());
            imgAva.setImage(image);
        }

        // achievements (chưa update khi user dùng app:>)
        if (res.getAchievements() != null && !res.getAchievements().isEmpty()) {
            if (res.getAchievements().size() > 0)
                txtMaster.setText(res.getAchievements().get(0).getTitle());

            if (res.getAchievements().size() > 1)
                txtQuickLearner.setText(res.getAchievements().get(1).getTitle());

            if (res.getAchievements().size() > 2)
                txtStreak.setText(res.getAchievements().get(2).getTitle());
        }
    }

    @FXML
    void handleEditProfile(ActionEvent event) {
        // chọn avatar
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Avatar");

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedAvatarPath = file.getAbsolutePath();

            Image image = new Image("file:" + selectedAvatarPath);
            imgAva.setImage(image);
        }

        // update profile
        ProfileRequest req = new ProfileRequest();
        req.setEmail(UserSession.getInstance().getEmail());
        req.setUsername(txtName.getText());
        req.setAvatarPath(selectedAvatarPath);

        boolean success = profileService.updateProfile(req);

        if (success) {
            loadProfile();
            System.out.println("Update success");
        } else {
            System.out.println("Update failed");
        }
    }
}