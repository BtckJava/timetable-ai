package com.ocr.javafx.dto.response;

import java.util.List;

public class ProfileResponse {

    private String username;
    private String email;
    private String avatarPath;

    private int totalHours;
    private int completedPlans;
    private int currentStreak;
    private int skillsLearned;

    private List<AchievementResponse> achievements;

    public ProfileResponse() {
    }

    public ProfileResponse(String username, String email, String avatarPath,
                           int totalHours, int completedPlans,
                           int currentStreak, int skillsLearned,
                           List<AchievementResponse> achievements) {
        this.username = username;
        this.email = email;
        this.avatarPath = avatarPath;
        this.totalHours = totalHours;
        this.completedPlans = completedPlans;
        this.currentStreak = currentStreak;
        this.skillsLearned = skillsLearned;
        this.achievements = achievements;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public int getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(int totalHours) {
        this.totalHours = totalHours;
    }

    public int getCompletedPlans() {
        return completedPlans;
    }

    public void setCompletedPlans(int completedPlans) {
        this.completedPlans = completedPlans;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getSkillsLearned() {
        return skillsLearned;
    }

    public void setSkillsLearned(int skillsLearned) {
        this.skillsLearned = skillsLearned;
    }

    public List<AchievementResponse> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<AchievementResponse> achievements) {
        this.achievements = achievements;
    }
}