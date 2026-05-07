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

        List<Object[]> dailyStatus = scheduleSlotRepository.findDailyCompletionStatus(user.getId());
        int streak = calculateStreak(dailyStatus);

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

        res.setAchievements(buildAchievements((int) hours, completedPlans, streak));
        return res;
    }

    private List<AchievementResponse> buildAchievements(int hours, int plans, int streak) {
        List<AchievementResponse> list = new ArrayList<>();

        // 1. Kế hoạch (Type: plan)
        list.add(calculateCustomTier(plans, new int[]{1, 5, 15, 30}, "plan",
                new String[]{"Beginner Planner", "Goal Getter", "Strategy Master", "Planning Legend"},
                new String[]{
                        "Completed 1 learning plan.",
                        "Completed 5 learning plans.",
                        "Completed 15 learning plans.",
                        "Completed 30 learning plans."
                }));
        // 2. Chuỗi ngày (Type: streak)
        list.add(calculateCustomTier(streak, new int[]{3, 7, 15, 30}, "streak",
                new String[]{"Habit Starter", "Weekly Warrior", "Unstoppable", "Consistency King"},
                new String[]{
                        "Maintain a 3-day streak.",
                        "Reach a 7-day streak.",
                        "Build a 15-day streak.",
                        "Achieve a 30-day ."
                }));
        // 3. Kỹ năng (Type: skill - dùng icon plan hoặc tạo mới)
        list.add(calculateCustomTier(hours, new int[]{10, 50, 150, 300}, "hour",
                new String[]{"Focus Novice", "Deep Learner", "Flow Specialist", "Zen Master"},
                new String[]{
                        "Log 10 hours of focused study.",
                        "Accumulate 50 hours of deep work.",
                        "Reach 150 focus hours.",
                        "Clock in 300 focus hours."
                }));
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

    private int calculateStreak(List<Object[]> dailyStatus) {
        if (dailyStatus == null || dailyStatus.isEmpty()) return 0;

        int streak = 0;
        LocalDate today = LocalDate.now();

        LocalDate latestTaskDate = (LocalDate) dailyStatus.get(0)[0];

        if (latestTaskDate.isBefore(today.minusDays(1))) {
            return 0;
        }

        for (Object[] row : dailyStatus) {
            LocalDate date = (LocalDate) row[0];
            int isFullyCompleted = ((Number) row[1]).intValue();

            if (isFullyCompleted == 1) {
                streak++;
            } else {
                if (date.equals(today)) {
                    continue;
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
        if (req.getAvatarPath() != null){
            user.setAvatarPath(req.getAvatarPath());
            System.out.println("SUCCESS");
        }
        userRepository.update(user);
        sessionManager.setCurrentUser(user);
        return true;
    }
}