package com.ocr.javafx.service;

import com.ocr.javafx.entity.ScheduleSlot;
import com.ocr.javafx.enums.LearningPlanStatus;
import com.ocr.javafx.repository.LearningPlanRepository;
import com.ocr.javafx.repository.ScheduleSlotRepository;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class StatsRowService {

    private final ScheduleSlotRepository scheduleSlotRepository;
    private final LearningPlanRepository learningPlanRepository;

    public StatsRowService(ScheduleSlotRepository scheduleSlotRepository,
                           LearningPlanRepository learningPlanRepository) {
        this.scheduleSlotRepository = scheduleSlotRepository;
        this.learningPlanRepository = learningPlanRepository;
    }

    public double getTotalLearningHours(Long userId) {

            List<ScheduleSlot> sessions = scheduleSlotRepository.findByUserId(userId);

            double totalMinutes = 0;

            for (ScheduleSlot s : sessions) {
                if(s.isCompleted()) {
                    long sessionDuration = Duration.between(s.getStartTime(), s.getEndTime()).toMinutes();
                    totalMinutes += sessionDuration;
                }
            }

            return totalMinutes / 60.0;
        }

    public long getCompletedPlans(Long userId) {
        return learningPlanRepository.countByUserIdAndStatus(userId, LearningPlanStatus.COMPLETED);
    }

    public long getInProgressPlans(Long userId) {
        return learningPlanRepository.countByUserIdAndStatus(userId, LearningPlanStatus.IN_PROGRESS);
    }

    public double getProgress(Long userId) {
        long completed = getCompletedPlans(userId);
        long total = learningPlanRepository.countByUserId(userId);
        long not_started = learningPlanRepository.countByUserIdAndStatus(userId, LearningPlanStatus.NOT_STARTED);
        long startedPlans = total - not_started;

        if (startedPlans == 0) return 0;
        return (completed * 100.0) / startedPlans;
    }
}