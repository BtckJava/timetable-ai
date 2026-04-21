package com.ocr.javafx.service;

import com.ocr.javafx.dto.request.ProfileRequest;
import com.ocr.javafx.dto.response.AchievementResponse;
import com.ocr.javafx.dto.response.ProfileResponse;
import com.ocr.javafx.entity.User;
import com.ocr.javafx.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class ProfileService {

    private final UserRepository userRepository = new UserRepository();

    public ProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) return null;

        ProfileResponse res = new ProfileResponse();

        // info
        res.setUsername(user.getUsername());
        res.setEmail(user.getEmail());
        res.setAvatarPath(user.getAvatarPath());

        // stats (demo, chua update)
        res.setTotalHours(user.getTotalHours());
        res.setCompletedPlans(user.getCompletedPlans());
        res.setCurrentStreak(user.getCurrentStreak());
        res.setSkillsLearned(user.getSkillsLearned());

        // achievements
        res.setAchievements(buildAchievements());

        return res;
    }

    // update profile bằng email
    public boolean updateProfile(ProfileRequest req) {
        User user = userRepository.findByEmail(req.getEmail());
        if (user == null) return false;

        user.setUsername(req.getUsername());

        if (req.getAvatarPath() != null) {
            user.setAvatarPath(req.getAvatarPath());
        }

        userRepository.update(user);
        return true;
    }

    // achievements
    private List<AchievementResponse> buildAchievements() {
        List<AchievementResponse> list = new ArrayList<>();

        list.add(new AchievementResponse("Master", "Completed many plans", "trophy"));
        list.add(new AchievementResponse("Quick Learner", "Fast progress", "flash"));
        list.add(new AchievementResponse("Consistent", "Keep streak", "fire"));

        return list;
    }
}