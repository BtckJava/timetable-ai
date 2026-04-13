package com.ocr.javafx.backend.service;

import com.ocr.javafx.backend.dto.CreatePlanRequest;
import com.ocr.javafx.backend.dto.StudyPlanResponse;
import com.ocr.javafx.backend.entity.StudyPlan;
import com.ocr.javafx.backend.entity.User;
import com.ocr.javafx.backend.repository.StudyPlanRepository;
import com.ocr.javafx.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class StudyPlanService {

    private final StudyPlanRepository studyPlanRepository;
    private final UserRepository userRepository;

    public StudyPlanService(StudyPlanRepository studyPlanRepository, UserRepository userRepository) {
        this.studyPlanRepository = studyPlanRepository;
        this.userRepository = userRepository;
    }

    public StudyPlanResponse createPlan(CreatePlanRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getUserId()));

        StudyPlan plan = new StudyPlan();
        plan.setUser(user);
        plan.setDomain(request.getDomain());
        plan.setGoal(request.getGoal());
        plan.setLevel(request.getLevel());
        plan.setDurationDays(request.getDurationDays());
        StudyPlan saved = studyPlanRepository.save(plan);

        return StudyPlanResponse.builder()
                .id(saved.getId())
                .userId(saved.getUser().getId())
                .domain(saved.getDomain())
                .goal(saved.getGoal())
                .level(saved.getLevel())
                .durationDays(saved.getDurationDays())
                .build();
    }
}
