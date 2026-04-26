package com.ocr.javafx.service;

import com.ocr.javafx.config.SessionManager;
import com.ocr.javafx.dto.request.ProfileRequest;
import com.ocr.javafx.dto.response.AchievementResponse;
import com.ocr.javafx.dto.response.ProfileResponse;
import com.ocr.javafx.entity.User;
import com.ocr.javafx.repository.LearningPlanRepository;
import com.ocr.javafx.repository.ScheduleSlotRepository;
import com.ocr.javafx.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProfileService {

    private final UserRepository userRepository;
    private final LearningPlanRepository learningPlanRepository;
    private final ScheduleSlotRepository scheduleSlotRepository;
    private final SessionManager sessionManager;

    public ProfileService(UserRepository userRepository,
                          LearningPlanRepository learningPlanRepository,
                          ScheduleSlotRepository scheduleSlotRepository,
                          SessionManager sessionManager) {
        this.userRepository = userRepository;
        this.learningPlanRepository = learningPlanRepository;
        this.scheduleSlotRepository = scheduleSlotRepository;
        this.sessionManager = sessionManager;
    }

    public ProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) return null;

        int actualCompletedPlans = (int) learningPlanRepository.countByUserIdAndStatus(
                user.getId(), com.ocr.javafx.enums.LearningPlanStatus.COMPLETED);

        int actualTotalHours = (int) scheduleSlotRepository.sumTotalHoursByUserId(user.getId());

        List<LocalDate> learnedDates = scheduleSlotRepository.findDistinctDatesByUserId(user.getId());
        int actualStreak = calculateStreak(learnedDates);

        // TÍNH TOÁN DYNAMIC CHO SKILLS (Vì ta đã xóa field này trong User)
        int actualSkills = (int) scheduleSlotRepository.findByUserId(user.getId())
                .stream()
                .map(slot -> slot.getTopic())
                .filter(topic -> topic != null && !topic.isEmpty())
                .distinct()
                .count();

        ProfileResponse res = new ProfileResponse();
        res.setUsername(user.getUsername());
        res.setEmail(user.getEmail());
        res.setAvatarPath(user.getAvatarPath());

        res.setTotalHours(actualTotalHours);
        res.setCompletedPlans(actualCompletedPlans);
        res.setCurrentStreak(actualStreak);
        res.setSkillsLearned(actualSkills); // Dùng biến vừa tính ở trên

        res.setAchievements(buildAchievements(actualTotalHours, actualCompletedPlans, actualStreak));

        return res;
    }

    private List<AchievementResponse> buildAchievements(int hours, int plans, int streak) {
        List<AchievementResponse> list = new ArrayList<>();
        list.add(calculateTier("Quick Learner", plans, new int[]{1, 5, 10, 25}, "plan", "plans"));
        list.add(calculateTier("Streak Master", streak, new int[]{3, 7, 15, 30}, "streak", "days"));
        list.add(calculateTier("Time Lord", hours, new int[]{5, 20, 50, 150}, "hour", "hours"));
        return list;
    }

    private AchievementResponse calculateTier(String title, int current, int[] thresholds, String type, String unit) {
        int level = 0;
        for (int t : thresholds) {
            if (current >= t) level++;
        }
        boolean isUnlocked = level > 0;
        String description;
        if (!isUnlocked) {
            description = "Reach " + thresholds[0] + " " + unit + " to unlock";
        } else {
            if (level < thresholds.length) {
                int nextGoal = thresholds[level];
                description = String.format("Level %d: %d/%d %s", level, current, nextGoal, unit);
            } else {
                description = String.format("Level %d (Max): %d %s completed!", level, current, unit);
            }
        }
        return new AchievementResponse(title, description, type, level, isUnlocked);
    }

    private int calculateStreak(List<LocalDate> dates) {
        if (dates == null || dates.isEmpty()) return 0;
        int streak = 0;
        LocalDate today = LocalDate.now();
        LocalDate firstDate = dates.get(0);
        if (!firstDate.equals(today) && !firstDate.equals(today.minusDays(1))) {
            return 0;
        }
        for (int i = 0; i < dates.size(); i++) {
            if (i == 0) {
                streak = 1;
            } else {
                if (dates.get(i - 1).minusDays(1).equals(dates.get(i))) {
                    streak++;
                } else {
                    break;
                }
            }
        }
        return streak;
    }

    public boolean updateProfile(ProfileRequest req) {
        User user = userRepository.findByEmail(req.getEmail());
        if (user == null) return false;
        user.setUsername(req.getUsername());
        if (req.getAvatarPath() != null) user.setAvatarPath(req.getAvatarPath());
        userRepository.update(user);
        sessionManager.setCurrentUser(user);
        return true;
    }
}