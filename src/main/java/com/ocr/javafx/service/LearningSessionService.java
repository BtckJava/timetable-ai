package com.ocr.javafx.service;

import com.ocr.javafx.entity.LearningSession;
import com.ocr.javafx.repository.LearningSessionRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LearningSessionService {
    private final LearningSessionRepository repository;

    public LearningSessionService(LearningSessionRepository repository) {
        this.repository = repository;
    }

    public List<LearningSession> getSessionsByUser(Long userId) {
        return repository.findByUserId(userId);
    }

    public void createSession(LearningSession session) {
        repository.save(session);
    }

    public Map<String, Double> getLearningHoursByDay(Long userId) {
        List<LearningSession> sessions = repository.findByUserId(userId);

        Map<String, Double> result = new HashMap<>();

        for (LearningSession s : sessions) {
            String date = s.getSessionTime().toLocalDate().toString();
            double hours = s.getDurationMinutes();

            result.put(date, result.getOrDefault(date, 0.0) + hours);
        }

        return result;
    }
}
