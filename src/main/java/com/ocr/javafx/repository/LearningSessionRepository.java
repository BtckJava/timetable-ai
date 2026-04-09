package com.ocr.javafx.repository;

import com.ocr.javafx.dto.response.LearningHoursResponse;
import com.ocr.javafx.entity.LearningSession;

import java.util.List;

public class LearningSessionRepository {
    public List<LearningHoursResponse> getLearningHoursPerDay(int userId) {
        // TODO: group sessions by date and sum duration
        return List.of();
    }

    public List<LearningSession> findByUserId(Long userId) {
        return List.of();      
    }


    public void save(LearningSession session) {
    }
}
