package com.ocr.javafx.dto.response;

public class AchievementResponse {

    private String title;
    private String description;
    private String iconType; // "plan", "streak", hoặc "hour"
    private int level;       // 0: Locked, 1: Bronze, 2: Silver, 3: Gold, 4: Diamond
    private boolean isUnlocked;

    public AchievementResponse() {
    }

    public AchievementResponse(String title, String description, String iconType, int level, boolean isUnlocked) {
        this.title = title;
        this.description = description;
        this.iconType = iconType;
        this.level = level;
        this.isUnlocked = isUnlocked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconType() {
        return iconType;
    }

    public void setIconType(String iconType) {
        this.iconType = iconType;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }
}