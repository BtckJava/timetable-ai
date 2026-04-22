package com.ocr.javafx.service;

import com.ocr.javafx.entity.ScheduleSlot;
import com.ocr.javafx.repository.ScheduleSlotRepository;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleSlotService {
    private final ScheduleSlotRepository repository;

    public ScheduleSlotService(ScheduleSlotRepository repository) {
        this.repository = repository;
    }

    public Map<DayOfWeek, Double> getLearningHoursByDay(Long userId) {
        List<ScheduleSlot> sessions = repository.findByUserId(userId);

        Map<DayOfWeek, Double> result = new HashMap<>();

        for (ScheduleSlot s : sessions) {
            if(s.isCompleted()) {
                long sessionDuration = Duration.between(s.getStartTime(), s.getEndTime()).toMinutes();
                DayOfWeek day = s.getDate().getDayOfWeek();

                double hours = sessionDuration / 60.0;

                result.put(day, result.getOrDefault(day, 0.0) + hours);
            }
        }

        return result;
    }

    public Map<String, Double> getSkillDistribution(Long userId){
        List<ScheduleSlot> sessions = repository.findByUserId(userId);

        Map<String, Double> result = new HashMap<>();

        for (ScheduleSlot s : sessions) {

            long sessionDuration = Duration.between(s.getStartTime(), s.getEndTime()).toMinutes();
            String name = s.getPlan().getTitle();

            double hours = sessionDuration / 60.0;

            result.put(name, result.getOrDefault(name, 0.0) + hours);
        }

        return result;
    }
}
