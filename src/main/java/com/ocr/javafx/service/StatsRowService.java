package com.ocr.javafx.service;

import com.ocr.javafx.dto.response.LearningHoursResponse;
import com.ocr.javafx.repository.LearningPlanRepository;
import com.ocr.javafx.repository.LearningSessionRepository;

public class StatsRowService {

    private final LearningSessionRepository learningSessionRepository;
    private final LearningPlanRepository learningPlanRepository;

    public StatsRowService(LearningSessionRepository learningSessionRepository,
                           LearningPlanRepository learningPlanRepository) {
        this.learningSessionRepository = learningSessionRepository;
        this.learningPlanRepository = learningPlanRepository;
    }

    public double getLearningHours(Long userId) {
        return learningSessionRepository.getLearningHoursPerDay(userId).stream()
                .mapToDouble(LearningHoursResponse::getHours)
                .sum();
    }

    public long getCompletedPlans(Long userId) {
        return learningPlanRepository.countByUserIdAndStatus(userId, "COMPLETED");
    }

    public long getInProgressPlans(Long userId) {
        return learningPlanRepository.countByUserIdAndStatus(userId, "IN_PROGRESS");
    }

    public double getProgress(Long userId) {
        long completed = getCompletedPlans(userId);
        long total = learningPlanRepository.countByUserId(userId);

        if (total == 0) return 0;
        return (completed * 100.0) / total;
    }
}