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

        int completedPlans = (int) learningPlanRepository.countByUserIdAndStatus(
                user.getId(), com.ocr.javafx.enums.LearningPlanStatus.COMPLETED);

        double hours = scheduleSlotRepository.sumTotalHoursByUserId(user.getId());

        List<java.time.LocalDate> dates = scheduleSlotRepository.findDistinctDatesByUserId(user.getId());
        int streak = calculateStreak(dates);

        // Tính số skills từ danh sách topic của các slot đã xong
        int skills = (int) scheduleSlotRepository.findByUserId(user.getId()).stream()
                .filter(s -> s.isCompleted() && s.getTopic() != null)
                .map(s -> s.getTopic().trim().toLowerCase())
                .distinct()
                .count();

        ProfileResponse res = new ProfileResponse();
        res.setUsername(user.getUsername());
        res.setEmail(user.getEmail());
        res.setAvatarPath(user.getAvatarPath());
        res.setTotalHours((int) hours);
        res.setCompletedPlans(completedPlans);
        res.setCurrentStreak(streak);
        res.setSkillsLearned(skills);

        res.setAchievements(buildAchievements((int) hours, completedPlans, streak, skills));
        return res;
    }

    private List<AchievementResponse> buildAchievements(int hours, int plans, int streak, int skills) {
        List<AchievementResponse> list = new ArrayList<>();

        // 1. Kế hoạch (Type: plan)
        list.add(calculateCustomTier(plans, new int[]{1, 5, 12, 25}, "plan",
                new String[]{"Starter", "Quick Learner", "Professional", "Plan Legend"},
                new String[]{"Completed your first plan", "5 plans completed", "12 plans finished", "25+ plans master"}));

        // 2. Chuỗi ngày (Type: streak)
        list.add(calculateCustomTier(streak, new int[]{3, 7, 15, 30}, "streak",
                new String[]{"Starter", "7-Day Streak", "Consistent", "Unstoppable"},
                new String[]{"3 days in a row", "A full week of learning", "15 days of discipline", "30 days! You are a beast"}));

        // 3. Kỹ năng (Type: skill - dùng icon plan hoặc tạo mới)
        list.add(calculateCustomTier(skills, new int[]{5, 10, 20, 50}, "plan",
                new String[]{"Learner", "Expert", "Master", "Grandmaster"},
                new String[]{"5 skills acquired", "10 skills learned", "Mastered 20+ skills", "50+ skills: Living Library"}));

        return list;
    }

    private AchievementResponse calculateCustomTier(int current, int[] thresholds, String iconType, String[] titles, String[] descriptions) {
        int level = 0;
        for (int t : thresholds) {
            if (current >= t) level++;
        }

        boolean isUnlocked = level > 0;
        String finalTitle = isUnlocked ? titles[level - 1] : "Locked";
        String finalDesc = isUnlocked ? descriptions[level - 1] : "Reach " + thresholds[0] + " to unlock";

        // Trả về DTO để Controller hiển thị
        return new AchievementResponse(finalTitle, finalDesc, iconType, level, isUnlocked);
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