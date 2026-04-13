package com.ocr.javafx.backend.controller;

import com.ocr.javafx.backend.dto.AiGenerateResponse;
import com.ocr.javafx.backend.dto.CreatePlanRequest;
import com.ocr.javafx.backend.dto.GenerateScheduleRequest;
import com.ocr.javafx.backend.dto.ScheduleSlotDto;
import com.ocr.javafx.backend.dto.StudyPlanResponse;
import com.ocr.javafx.backend.service.ScheduleService;
import com.ocr.javafx.backend.service.StudyPlanService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StudyPlanController {

    private final StudyPlanService studyPlanService;
    private final ScheduleService scheduleService;

    public StudyPlanController(StudyPlanService studyPlanService, ScheduleService scheduleService) {
        this.studyPlanService = studyPlanService;
        this.scheduleService = scheduleService;
    }

    @PostMapping("/plans")
    public StudyPlanResponse createPlan(@RequestBody CreatePlanRequest request) {
        return studyPlanService.createPlan(request);
    }

    @PostMapping("/ai/generate")
    public AiGenerateResponse generateSchedule(@RequestBody GenerateScheduleRequest request) {
        return scheduleService.generateAndSave(request.getPlanId());
    }

    @GetMapping("/schedule/{planId}")
    public List<ScheduleSlotDto> getSchedule(@PathVariable Long planId) {
        return scheduleService.getSchedule(planId);
    }
}
