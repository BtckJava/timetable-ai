package com.ocr.javafx.service;

import java.time.DayOfWeek;
import java.util.Map;

public class LearningChartService {
    private final ScheduleSlotService scheduleSlotService;

    public LearningChartService(ScheduleSlotService scheduleSlotService) {
        this.scheduleSlotService = scheduleSlotService;
    }

    public Map<DayOfWeek, Double> getWeeklyLearningData(Long userId) {
        return scheduleSlotService.getLearningHoursByDay(userId);
    }

    public Map<String, Double> getSkillDistribution(Long userId) {
        return scheduleSlotService.getSkillDistribution(userId);
    }
}
