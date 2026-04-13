package com.ocr.javafx.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocr.javafx.backend.dto.AiGenerateResponse;
import com.ocr.javafx.backend.dto.ScheduleSlotDto;
import com.ocr.javafx.backend.dto.ai.AiSchedulePayload;
import com.ocr.javafx.backend.dto.ai.AiScheduleSlotPayload;
import com.ocr.javafx.backend.entity.ScheduleSlot;
import com.ocr.javafx.backend.entity.StudyPlan;
import com.ocr.javafx.backend.repository.ScheduleSlotRepository;
import com.ocr.javafx.backend.repository.StudyPlanRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleService {

    private static final Logger log = LoggerFactory.getLogger(ScheduleService.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final StudyPlanRepository studyPlanRepository;
    private final ScheduleSlotRepository scheduleSlotRepository;
    private final PromptBuilder promptBuilder;
    private final AIService aiService;
    private final ObjectMapper objectMapper;

    public ScheduleService(StudyPlanRepository studyPlanRepository,
                           ScheduleSlotRepository scheduleSlotRepository,
                           PromptBuilder promptBuilder,
                           AIService aiService,
                           ObjectMapper objectMapper) {
        this.studyPlanRepository = studyPlanRepository;
        this.scheduleSlotRepository = scheduleSlotRepository;
        this.promptBuilder = promptBuilder;
        this.aiService = aiService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public AiGenerateResponse generateAndSave(Long planId) {
        StudyPlan plan = studyPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found: " + planId));

        List<ScheduleSlotDto> existing = findRecentSchedule(planId);
        String prompt = promptBuilder.build(
                plan.getDomain(),
                plan.getGoal(),
                plan.getLevel(),
                plan.getDurationDays(),
                3,
                existing
        );

        String aiJson = aiService.generateScheduleJson(prompt);
        List<ScheduleSlot> generated = parseAiJson(aiJson, plan);

        int saved = 0;
        int skippedConflict = 0;
        for (ScheduleSlot slot : generated) {
            if (hasConflict(planId, slot.getDate(), slot.getStartTime(), slot.getEndTime())) {
                skippedConflict++;
                log.warn("Skipped conflict slot: planId={} date={} {}-{}", planId, slot.getDate(), slot.getStartTime(), slot.getEndTime());
                continue;
            }
            scheduleSlotRepository.save(slot);
            saved++;
        }

        return AiGenerateResponse.builder()
                .planId(planId)
                .totalGenerated(generated.size())
                .totalSaved(saved)
                .totalSkippedConflict(skippedConflict)
                .build();
    }

    public List<ScheduleSlotDto> getSchedule(Long planId) {
        return scheduleSlotRepository.findByStudyPlanIdOrderByDateAscStartTimeAsc(planId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<ScheduleSlotDto> findRecentSchedule(Long planId) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(60);
        return scheduleSlotRepository.findByStudyPlanInDateRange(planId, start, end)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private List<ScheduleSlot> parseAiJson(String json, StudyPlan plan) {
        try {
            AiSchedulePayload payload = objectMapper.readValue(json, AiSchedulePayload.class);
            List<ScheduleSlot> result = new ArrayList<>();
            for (AiScheduleSlotPayload aiSlot : payload.getScheduleSlots()) {
                ScheduleSlot slot = new ScheduleSlot();
                slot.setStudyPlan(plan);
                slot.setDate(LocalDate.parse(aiSlot.getDate()));
                slot.setStartTime(LocalTime.parse(aiSlot.getStartTime(), TIME_FORMATTER));
                slot.setEndTime(LocalTime.parse(aiSlot.getEndTime(), TIME_FORMATTER));
                slot.setTopic(aiSlot.getTopic());
                slot.setSubTopic(aiSlot.getSubTopic());
                slot.setResourceUrl(aiSlot.getResourceUrl());
                result.add(slot);
            }
            return result;
        } catch (Exception e) {
            throw new IllegalStateException("Cannot parse AI response JSON", e);
        }
    }

    private boolean hasConflict(Long planId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        return scheduleSlotRepository.countConflicts(planId, date, startTime, endTime) > 0;
    }

    private ScheduleSlotDto toDto(ScheduleSlot slot) {
        ScheduleSlotDto dto = new ScheduleSlotDto();
        dto.setId(slot.getId());
        dto.setDate(slot.getDate());
        dto.setStartTime(slot.getStartTime());
        dto.setEndTime(slot.getEndTime());
        dto.setTopic(slot.getTopic());
        dto.setSubTopic(slot.getSubTopic());
        dto.setResourceUrl(slot.getResourceUrl());
        return dto;
    }
}
