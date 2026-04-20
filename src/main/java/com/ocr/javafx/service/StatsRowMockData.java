package com.ocr.javafx.service;

import com.ocr.javafx.repository.LearningPlanRepository;
import com.ocr.javafx.repository.LearningSessionRepository;

public class StatsRowMockData extends StatsRowService {

    public StatsRowMockData(LearningSessionRepository learningSessionRepository, LearningPlanRepository learningPlanRepository) {
        super(learningSessionRepository, learningPlanRepository);
    }

    @Override
    public double getLearningHours(Long userId) {
        return 42.5;
    }

    @Override
    public long getCompletedPlans(Long userId) {
        return 8;
    }

    @Override
    public long getInProgressPlans(Long userId) {
        return 3;
    }

    @Override
    public double getProgress(Long userId) {
        return 76.0;
    }
}
